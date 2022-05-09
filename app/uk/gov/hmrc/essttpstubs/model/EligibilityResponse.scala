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

package uk.gov.hmrc.essttpstubs.model

import play.api.libs.json.{ Json, OFormat }

//todo should we move this into cor and make more type safe, i.e. idType: IdType?
//also, probably poorly named, since we use it to insert into the db, could be misleading
final case class EligibilityResponse(
  idType: String,
  idNumber: String,
  regimeType: String,
  processingDate: String,
  customerDetails: CustomerDetails,
  minPlanLengthMonths: Int,
  maxPlanLengthMonths: Int,
  eligibilityStatus: EligibilityStatus,
  eligibilityRules: EligibilityRules,
  chargeTypeAssessment: Seq[ChargeTypeAssessment])

object EligibilityResponse {
  implicit val format: OFormat[EligibilityResponse] = Json.format[EligibilityResponse]
}

final case class CustomerDetails(country: String, postCode: String)

object CustomerDetails {
  implicit val format: OFormat[CustomerDetails] = Json.format[CustomerDetails]
}

final case class EligibilityStatus(eligibilityPass: Boolean)

object EligibilityStatus {
  implicit val format: OFormat[EligibilityStatus] = Json.format[EligibilityStatus]
}

final case class EligibilityRules(
  hasRlsOnAddress: Boolean,
  rlsReason: String,
  markedAsInsolvent: Boolean,
  isLessThanMinDebtAllowance: Boolean,
  isMoreThanMaxDebtAllowance: Boolean,
  disallowedChargeLocks: Boolean,
  existingTTP: Boolean,
  exceedsMaxDebtAge: Boolean,
  eligibleChargeType: Boolean,
  missingFiledReturns: Boolean)

object EligibilityRules {
  implicit val format: OFormat[EligibilityRules] = Json.format[EligibilityRules]
}

final case class ChargeTypeAssessment(taxPeriodFrom: String, taxPeriodTo: String, debtTotalAmount: Int, disallowedChargeLocks: Seq[DisallowedChargeLocks])

object ChargeTypeAssessment {
  implicit val format: OFormat[ChargeTypeAssessment] = Json.format[ChargeTypeAssessment]
}

final case class DisallowedChargeLocks(
  chargeId: String,
  mainTrans: String,
  mainTransDesc: String,
  subTrans: String,
  subTransDesc: String,
  outstandingDebtAmount: Int,
  interestStartDate: String,
  accruedInterestToDate: Int,
  chargeLocks: ChargeLocks)

object DisallowedChargeLocks {
  implicit val format: OFormat[DisallowedChargeLocks] = Json.format[DisallowedChargeLocks]
}

final case class ChargeLock(status: Boolean, reason: String)

object ChargeLock {
  implicit val format: OFormat[ChargeLock] = Json.format[ChargeLock]
}

final case class ChargeLocks(paymentLock: ChargeLock, clearingLock: ChargeLock, interestLock: ChargeLock, dunningLock: ChargeLock)

object ChargeLocks {
  implicit val format: OFormat[ChargeLocks] = Json.format[ChargeLocks]
}
