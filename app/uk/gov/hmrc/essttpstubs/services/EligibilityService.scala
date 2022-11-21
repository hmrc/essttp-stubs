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

import essttp.rootmodel.AmountInPence
import essttp.rootmodel.ttp.affordablequotes.DueDate
import essttp.rootmodel.ttp.eligibility.{CustomerDetail, EmailSource, RegimeDigitalCorrespondence}
import essttp.rootmodel.ttp._
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
    eligibilityRepo.findEligibilityDataByTaxRef(eligibilityRequest.idValue)
      .map(f => f.map(_.eligibilityCheckResult))

  def removeAllRecordsFromEligibilityDb(): Future[Unit] = eligibilityRepo.removeAllRecords().map(_ => ())

}

object EligibilityService {
  def defaultEligibleResponse(regimeType: RegimeType, taxId: String): EligibilityCheckResult = {
    val identification: List[Identification] = regimeType match {
      case RegimeType("PAYE") =>
        List(Identification(IdType("EMPREF"), IdValue(taxId)), Identification(IdType("BROCS"), IdValue(taxId)))
      case RegimeType("VATC") =>
        List(Identification(IdType("VRN"), IdValue(taxId)))
      case _ => // just default to epaye, we don't really care...
        List(Identification(IdType("EMPREF"), IdValue(taxId)), Identification(IdType("BROCS"), IdValue(taxId)))
    }
    EligibilityCheckResult(
      processingDateTime              = ProcessingDateTime(Instant.now().toString),
      identification                  = identification,
      customerPostcodes               = List(CustomerPostcode(addressPostcode = Postcode(SensitiveString("AA11AA")), postcodeDate = PostcodeDate("2022-01-01"))),
      regimePaymentFrequency          = PaymentPlanFrequencies.Monthly,
      paymentPlanFrequency            = PaymentPlanFrequencies.Monthly,
      paymentPlanMinLength            = PaymentPlanMinLength(1),
      paymentPlanMaxLength            = PaymentPlanMaxLength(6),
      eligibilityStatus               = EligibilityStatus(EligibilityPass(true)),
      eligibilityRules                = EligibilityRules(
        hasRlsOnAddress                   = false,
        markedAsInsolvent                 = false,
        isLessThanMinDebtAllowance        = false,
        isMoreThanMaxDebtAllowance        = false,
        disallowedChargeLockTypes         = false,
        existingTTP                       = false,
        chargesOverMaxDebtAge             = false,
        ineligibleChargeTypes             = false,
        missingFiledReturns               = false,
        hasInvalidInterestSignals         = Some(false),
        dmSpecialOfficeProcessingRequired = Some(false),
        noDueDatesReached                 = Some(false)
      ),
      chargeTypeAssessment            = List(
        ChargeTypeAssessment(
          TaxPeriodFrom("2020-08-13"),
          TaxPeriodTo("2020-08-14"),
          DebtTotalAmount(AmountInPence(123456)),
          List(Charges(
            chargeType           = ChargeType("InYearRTICharge-Tax"),
            mainType             = MainType("InYearRTICharge(FPS)"),
            chargeReference      = ChargeReference(taxId),
            mainTrans            = MainTrans("mainTrans"),
            subTrans             = SubTrans("subTrans"),
            outstandingAmount    = OutstandingAmount(AmountInPence(123456)),
            interestStartDate    = Some(InterestStartDate(LocalDate.parse("2017-03-07"))),
            dueDate              = DueDate(LocalDate.parse("2017-03-07")),
            accruedInterest      = AccruedInterest(AmountInPence(123)),
            ineligibleChargeType = IneligibleChargeType(false),
            chargeOverMaxDebtAge = ChargeOverMaxDebtAge(false),
            locks                = Some(
              List(
                Lock(
                  lockType                 = LockType("Payment"),
                  lockReason               = LockReason("Risk/Fraud"),
                  disallowedChargeLockType = DisallowedChargeLockType(false)
                )
              )
            ),
            dueDateNotReached    = Some(false)
          ))
        )
      ),
      customerDetails                 = Some(List(CustomerDetail(Some("bobross@joyofpainting.com"), Some(EmailSource.ETMP)))),
      regimeDigitalCorrespondence     = Some(RegimeDigitalCorrespondence(true)),
      futureChargeLiabilitiesExcluded = Some(false)
    )
  }

}

