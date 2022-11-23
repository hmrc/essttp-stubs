/*
 * Copyright 2022 HM Revenue & Customs
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package uk.gov.hmrc.essttpstubs.services

import cats.implicits._
import essttp.rootmodel.ttp.InterestAccrued
import essttp.rootmodel.ttp.affordablequotes._
import essttp.rootmodel.{AmountInPence, UpfrontPaymentAmount}
import essttp.rootmodel.dates.startdates.InstalmentStartDate
import play.api.Configuration
import uk.gov.hmrc.essttpstubs.services.AffordableQuotesService.noRegularCollectionsRuntimeError

import javax.inject.{Inject, Singleton}
import scala.annotation.tailrec
import scala.math.BigDecimal.RoundingMode
import scala.util.Try

@Singleton
class AffordableQuotesService @Inject() (config: Configuration) {

  private val baseInterestRate: BigDecimal =
    BigDecimal(config.get[String]("affordability.instalment-amounts.base-interest-rate"))

  private val additionalInterestRate: BigDecimal =
    BigDecimal(config.get[String]("affordability.instalment-amounts.additional-interest-rate"))

  private val totalInterestRate: BigDecimal = baseInterestRate + additionalInterestRate

  def calculateAffordableQuotes(affordableQuotesRequest: AffordableQuotesRequest): AffordableQuotesResponse = {
    val totalDebt: TotalDebt = TotalDebt(
      AmountInPence(affordableQuotesRequest.debtItemCharges.map(x => x.outstandingDebtAmount.value.value).sum + affordableQuotesRequest.accruedDebtInterest.value.value)
        .-(affordableQuotesRequest.initialPaymentAmount.map(_.value).getOrElse(AmountInPence(0)))
    )
    val interestPerMonthRound = for {
      interest <- toLong((totalDebt.value.inPounds / 100 * totalInterestRate) / 12)
    } yield AmountInPence(interest)
    val interestPerMonth = interestPerMonthRound match {
      case Right(value) => value
      case Left(error)  => throw new RuntimeException(error.message)
    }
    val initialCollection: Option[InitialCollection] =
      affordableQuotesRequest.initialPaymentAmount
        .flatMap { (someUpfrontPaymentAmount: UpfrontPaymentAmount) =>
          affordableQuotesRequest.initialPaymentDate
            .map(someUpfrontPaymentDate => InitialCollection(DueDate(someUpfrontPaymentDate.value), AmountDue(someUpfrontPaymentAmount.value)))
        }
    val allPossiblePlans: List[PaymentPlan] =
      computeAllPossiblePlans(affordableQuotesRequest, totalDebt, interestPerMonth, initialCollection)
        .filterNot(_.collections.regularCollections.exists(_.amountDue.value.inPounds < 1))
    val optimumPlan: PaymentPlan = optimumPaymentPlan(affordableQuotesRequest.paymentPlanAffordableAmount, allPossiblePlans) match {
      case Some(value) => value
      case None        => allPossiblePlans.find(p => p.planDuration.value === 1).getOrElse(throw new RuntimeException("There was no optimum plan, investigate..."))
    }
    val paymentPlans = deriveCollectionsToReturnBasedOnOptimum(optimumPlan, allPossiblePlans)
    paymentPlans match {
      case Right(valid)    => AffordableQuotesResponse(valid)
      case Left(someError) => throw new RuntimeException(someError.message)
    }
  }

  private def computeAllPossiblePlans(affordableQuotesRequest: AffordableQuotesRequest, totalDebt: TotalDebt, interestPerMonth: AmountInPence, initialCollection: Option[InitialCollection]): List[PaymentPlan] =
    (1 to 6).map { numberOfInstalments =>
      computeAPlan(numberOfInstalments, totalDebt, interestPerMonth, affordableQuotesRequest.paymentPlanStartDate, initialCollection, affordableQuotesRequest)
    }.toList

  private def computeAPlan(planDuration: Int, totalDebt: TotalDebt, interestPerMonth: AmountInPence, startDate: InstalmentStartDate, initialCollection: Option[InitialCollection], affordableQuotesRequest: AffordableQuotesRequest): PaymentPlan = {
    val totalInterest = AmountInPence(interestPerMonth.value * planDuration)
    val totalDebtPlusInterest = TotalDebtIncludingInterest(totalDebt.value.+(totalInterest))
    val collections = computeCollections(planDuration, totalDebtPlusInterest, startDate, initialCollection)
    PaymentPlan(
      numberOfInstalments = NumberOfInstalments(planDuration),
      planDuration        = PlanDuration(planDuration),
      totalDebt           = totalDebt,
      totalDebtIncInt     = totalDebtPlusInterest,
      planInterest        = PlanInterest(totalInterest),
      collections         = collections,
      instalments         = computeInstalments(affordableQuotesRequest, totalDebt, interestPerMonth, collections.regularCollections.size)
    )
  }

  private def computeCollections(planDuration: Int, totalDebtIncludingInterest: TotalDebtIncludingInterest, planStartDate: InstalmentStartDate, initialCollection: Option[InitialCollection]): Collection = {
    val amountDue = AmountDue(AmountInPence(totalDebtIncludingInterest.value.value / planDuration))
    Collection(
      initialCollection  = initialCollection,
      regularCollections = (1 to planDuration).map { x =>
        RegularCollection(
          DueDate(planStartDate.value.plusMonths(x)),
          amountDue
        )
      }.toList
    )
  }

  //optimum is the one with closest monthly amount from collection, which is less than or equal to affordable amount
  def optimumPaymentPlan(paymentPlanAffordableAmount: PaymentPlanAffordableAmount, paymentPlans: List[PaymentPlan]): Option[PaymentPlan] = {
    val collectionsLessThanAffordableAmount: List[PaymentPlan] =
      paymentPlans.filter(_.collections.regularCollections.headOption.getOrElse(noRegularCollectionsRuntimeError).amountDue.value.value <= paymentPlanAffordableAmount.value.value)

      def getClosest(remainingCollections: List[PaymentPlan]): Option[PaymentPlan] = {
        val sortedOnes = remainingCollections
          .sortWith(
            _.collections.regularCollections.headOption.getOrElse(noRegularCollectionsRuntimeError).amountDue.value.value >
              _.collections.regularCollections.headOption.getOrElse(noRegularCollectionsRuntimeError).amountDue.value.value
          )
        sortedOnes.headOption
      }

    getClosest(collectionsLessThanAffordableAmount)
  }

  def deriveCollectionsToReturnBasedOnOptimum(optimum: PaymentPlan, paymentPlans: List[PaymentPlan]): Either[AffordableQuotesService.CalculationError, List[PaymentPlan]] = {
    optimum.planDuration match {
      case PlanDuration(1) | PlanDuration(2) => Right(paymentPlans.filter(_.planDuration.value < 4))
      case PlanDuration(3)                   => Right(paymentPlans.filter(_.planDuration.value > 1).filter(_.planDuration.value < 5))
      case PlanDuration(4)                   => Right(paymentPlans.filter(_.planDuration.value > 2).filter(_.planDuration.value < 6))
      case PlanDuration(5) | PlanDuration(6) => Right(paymentPlans.filter(_.planDuration.value > 3).filter(_.planDuration.value < 7))
      case otherDuration                     => Left(AffordableQuotesService.CalculationError(s"Something went wrong, this match should only be for keeping the compiler happy... PlanDuration: [ ${otherDuration.toString} ]"))
    }
  }

  def computeInstalments(affordableQuotesRequest: AffordableQuotesRequest, totalDebt: TotalDebt, monthlyInterest: AmountInPence, numberOfInstalments: Int): List[Instalment] = {
    val monthlyAmount = AmountInPence(totalDebt.value.value / numberOfInstalments)
      @tailrec
      def createInstalments(list: List[Instalment], acc: Int): List[Instalment] = {
        if (list.size === numberOfInstalments) list
        else {
          val newInstalment: Instalment = Instalment(
            instalmentNumber          = InstalmentNumber(acc),
            dueDate                   = DueDate(affordableQuotesRequest.paymentPlanStartDate.value.plusMonths(acc)),
            instalmentInterestAccrued = InterestAccrued(monthlyInterest),
            instalmentBalance         = InstalmentBalance(totalDebt.value.-(AmountInPence(monthlyAmount.value * (acc - 1)))),
            debtItemChargeId          = affordableQuotesRequest.debtItemCharges.map(_.debtItemChargeId).headOption.getOrElse(throw new RuntimeException("There was no charge id in request...")),
            amountDue                 = AmountDue(monthlyAmount),
            debtItemOriginalDueDate   = DebtItemOriginalDueDate(affordableQuotesRequest.paymentPlanStartDate.value.minusYears(1)) // just made up
          )
          createInstalments(newInstalment :: list, acc + 1)
        }
      }

    createInstalments(List.empty, 1)
  }

  private def toLong(n: BigDecimal): Either[AffordableQuotesService.CalculationError, Long] =
    Try(n.setScale(0, RoundingMode.HALF_UP).toLongExact)
      .toEither
      .leftMap(e => AffordableQuotesService.CalculationError(s"Could not convert BigDecimal to Long: ${e.getMessage}"))
}

object AffordableQuotesService {
  sealed trait Error

  final case class CalculationError(message: String) extends Error

  def noRegularCollectionsRuntimeError: Nothing = throw new RuntimeException("There were no regular collections")
}
