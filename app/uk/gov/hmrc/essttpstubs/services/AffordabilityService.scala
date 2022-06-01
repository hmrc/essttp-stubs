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

import cats.syntax.either._

import play.api.Configuration
import uk.gov.hmrc.essttpstubs.model.{InstalmentAmountRequest, InstalmentAmounts}
import uk.gov.hmrc.essttpstubs.services.AffordabilityService.{BadRequestError, CalculationError, Error}

import javax.inject.{Inject, Singleton}
import scala.math.BigDecimal.RoundingMode
import scala.util.Try

@Singleton
class AffordabilityService @Inject() (config: Configuration) {

  private val baseInterestRate: BigDecimal =
    BigDecimal(config.get[String]("affordability.instalment-amounts.base-interest-rate"))

  private val additionalInterestRate: BigDecimal =
    BigDecimal(config.get[String]("affordability.instalment-amounts.additional-interest-rate"))

  private val totalInterestRate: BigDecimal = baseInterestRate + additionalInterestRate

  def calculateInstalmentAmounts(request: InstalmentAmountRequest): Either[Error, InstalmentAmounts] = {
    val totalDebtAmount = request.debtItemCharges.map(_.outstandingDebtAmount).sum
    val initialPaymentAmount = request.initialPaymentAmount.getOrElse(0)
    if (totalDebtAmount <= initialPaymentAmount)
      Left(BadRequestError("Total debt amount was less than or eqaul to the initial payment amount"))
    else if (request.minPlanLength > request.maxPlanLength)
      Left(BadRequestError("Min plan length was strictly greater than the max plan length"))
    else {
      val residualDebtAmount = BigDecimal(totalDebtAmount - initialPaymentAmount)
      val interestPerMonth = totalInterestRate * residualDebtAmount / BigDecimal(1200)

      val minInterest = request.minPlanLength * interestPerMonth
      val maxInterest = request.maxPlanLength * interestPerMonth

      val minInstalmentAmount = residualDebtAmount / request.maxPlanLength + maxInterest
      val maxInstalmentAmount = residualDebtAmount / request.minPlanLength + minInterest

      for {
        min <- toInt(minInstalmentAmount)
        max <- toInt(maxInstalmentAmount)
      } yield InstalmentAmounts(min, max)

    }

  }

  private def toInt(n: BigDecimal): Either[CalculationError, Int] =
    Try(n.setScale(0, RoundingMode.HALF_UP).toIntExact)
      .toEither
      .leftMap(e => CalculationError(s"Could not convert BigDecimal to Int: ${e.getMessage}"))

}

object AffordabilityService {

  sealed trait Error

  final case class BadRequestError(message: String) extends Error

  final case class CalculationError(message: String) extends Error
}
