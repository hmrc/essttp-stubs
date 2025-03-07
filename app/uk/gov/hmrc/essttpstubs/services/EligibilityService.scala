/*
 * Copyright 2023 HM Revenue & Customs
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

import cats.implicits.catsSyntaxEq
import essttp.rootmodel.ttp._
import essttp.rootmodel.ttp.affordablequotes.DueDate
import essttp.rootmodel.ttp.eligibility._
import essttp.rootmodel.{AmountInPence, Email}
import uk.gov.hmrc.crypto.Sensitive.SensitiveString
import uk.gov.hmrc.essttpstubs.model.EligibilityRequest
import uk.gov.hmrc.essttpstubs.repo.{EligibilityEntry, EligibilityRepo}

import java.time.{Instant, LocalDate}
import javax.inject.{Inject, Singleton}
import scala.concurrent.{ExecutionContext, Future}

@Singleton()
class EligibilityService @Inject() (eligibilityRepo: EligibilityRepo)(implicit executionContext: ExecutionContext) {

  def insertEligibilityData(eligibilityCheckResult: EligibilityCheckResult): Future[Unit] = {
    eligibilityRepo.insertEligibilityData(EligibilityEntry(eligibilityCheckResult, Instant.now())).map(_ => ())
  }

  def eligibilityData(eligibilityRequest: EligibilityRequest): Future[Option[EligibilityCheckResult]] =
    eligibilityRepo.findEligibilityDataByTaxRef(eligibilityRequest.identification)
      .map(f => f.map(_.eligibilityCheckResult))

  def removeAllRecordsFromEligibilityDb(): Future[Unit] = eligibilityRepo.removeAllRecords().map(_ => ())

}

object EligibilityService {
  def defaultEligibleResponse(regimeType: RegimeType, identificationList: List[Identification]): EligibilityCheckResult = {
    val identification: List[Identification] = regimeType match {
      case RegimeType.EPAYE => if (identificationList.length === 1) {
        identificationList ++ List(Identification(IdType("BROCS"), IdValue("someValue")))
      } else { sys.error("there should only be one item in the list for PAYE") }
      case RegimeType.VAT => if (identificationList.length === 1) {
        identificationList
      } else { sys.error("there should only be one item in the list for VAT") }
      case RegimeType.SA =>
        identificationList
      case RegimeType.SIMP =>
        identificationList
      case _ => // just default to epaye, we don't really care...
        identificationList ++ List(Identification(IdType("BROCS"), IdValue("someValue")))
    }
    EligibilityCheckResult(
      processingDateTime              = ProcessingDateTime(Instant.now().toString),
      identification                  = identification,
      customerPostcodes               = List(CustomerPostcode(addressPostcode = Postcode(SensitiveString("AA11AA")), postcodeDate = PostcodeDate(LocalDate.of(2022, 1, 1)))),
      regimePaymentFrequency          = PaymentPlanFrequencies.Monthly,
      paymentPlanFrequency            = PaymentPlanFrequencies.Monthly,
      paymentPlanMinLength            = PaymentPlanMinLength(1),
      paymentPlanMaxLength            = PaymentPlanMaxLength(6),
      eligibilityStatus               = EligibilityStatus(EligibilityPass(value = true)),
      eligibilityRules                = EligibilityRules(
        EligibilityRulesPart1(
          hasRlsOnAddress                       = false,
          markedAsInsolvent                     = false,
          isLessThanMinDebtAllowance            = false,
          isMoreThanMaxDebtAllowance            = false,
          disallowedChargeLockTypes             = false,
          existingTTP                           = false,
          chargesOverMaxDebtAge                 = Some(false),
          ineligibleChargeTypes                 = false,
          missingFiledReturns                   = false,
          hasInvalidInterestSignals             = Some(false),
          dmSpecialOfficeProcessingRequired     = Some(false),
          noDueDatesReached                     = false,
          cannotFindLockReason                  = Some(false),
          creditsNotAllowed                     = Some(false),
          isMoreThanMaxPaymentReference         = Some(false),
          chargesBeforeMaxAccountingDate        = Some(false),
          hasInvalidInterestSignalsCESA         = Some(false),
          hasDisguisedRemuneration              = Some(false),
          hasCapacitor                          = Some(false),
          dmSpecialOfficeProcessingRequiredCDCS = Some(false),
          isAnMtdCustomer                       = Some(false),
          dmSpecialOfficeProcessingRequiredCESA = Some(false)
        ),
        EligibilityRulesPart2(
          noMtditsaEnrollment = Some(false)
        )
      ),
      chargeTypeAssessment            = List(
        ChargeTypeAssessment(
          TaxPeriodFrom("2020-08-13"),
          TaxPeriodTo("2020-08-14"),
          DebtTotalAmount(AmountInPence(123456)),
          chargeReference = ChargeReference("someValue"),
          List(Charges(
            Charges1(
              chargeType              = ChargeType("InYearRTICharge-Tax"),
              mainType                = MainType("InYearRTICharge(FPS)"),
              mainTrans               = MainTrans("mainTrans"),
              subTrans                = SubTrans("subTrans"),
              outstandingAmount       = OutstandingAmount(AmountInPence(123456)),
              interestStartDate       = Some(InterestStartDate(LocalDate.parse("2017-03-07"))),
              dueDate                 = DueDate(LocalDate.parse("2017-03-07")),
              accruedInterest         = AccruedInterest(AmountInPence(123)),
              ineligibleChargeType    = IneligibleChargeType(value = false),
              chargeOverMaxDebtAge    = Some(ChargeOverMaxDebtAge(value = false)),
              locks                   = Some(
                List(
                  Lock(
                    lockType                 = LockType("Payment"),
                    lockReason               = LockReason("Risk/Fraud"),
                    disallowedChargeLockType = DisallowedChargeLockType(value = false)
                  )
                )
              ),
              dueDateNotReached       = false,
              isInterestBearingCharge = None
            ),
            Charges2(
              useChargeReference            = None,
              chargeBeforeMaxAccountingDate = None,
              ddInProgress                  = None,
              chargeSource                  = None,
              parentChargeReference         = None,
              parentMainTrans               = None,
              originalCreationDate          = None,
              tieBreaker                    = None,
              originalTieBreaker            = None,
              saTaxYearEnd                  = None,
              creationDate                  = None,
              originalChargeType            = None
            )
          ))
        )
      ),
      customerDetails                 = List(CustomerDetail(Some(Email(SensitiveString("bobross@joyofpainting.com"))), Some(EmailSource.ETMP))),
      individualDetails               = None,
      addresses                       = List(
        Address(
          addressType     = AddressType("Residential"),
          addressLine1    = None,
          addressLine2    = None,
          addressLine3    = None,
          addressLine4    = None,
          rls             = None,
          contactDetails  = Some(ContactDetail(
            telephoneNumber = None,
            fax             = None,
            mobile          = None,
            emailAddress    = Some(Email(SensitiveString("some@email"))),
            emailSource     = None,
            altFormat       = None
          )),
          postCode        = None,
          country         = None,
          postcodeHistory = List(
            PostcodeHistory(
              addressPostcode = Postcode(SensitiveString("POSTCODE")),
              postcodeDate    = PostcodeDate(LocalDate.now())
            )
          )
        )
      ),
      regimeDigitalCorrespondence     = RegimeDigitalCorrespondence(value = true),
      futureChargeLiabilitiesExcluded = false,
      invalidSignals                  = Some(List(InvalidSignals(signalType        = "xyz", signalValue = "123", signalDescription = "Description"))),
      chargeTypesExcluded             = None
    )
  }

}

