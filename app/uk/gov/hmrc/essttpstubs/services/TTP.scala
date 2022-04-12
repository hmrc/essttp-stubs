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

import play.api.libs.json.Json
import uk.gov.hmrc.essttpstubs.model._
import uk.gov.hmrc.essttpstubs.services.EligibilityService.EligibilityError
import uk.gov.hmrc.essttpstubs.ttp.model._


case class TTP(items: Map[TaxId, TtpEligibilityData]){

  def eligibilityData(taxID: TaxId, financials: Boolean): TtpEligibilityData = {
    val result = items.getOrElse(taxID, TTP.Default)
    if(financials) result else result.copy(chargeTypeAssessment = Nil)
  }

  def insertEligibilityData(taxId: TaxId, data: TtpEligibilityData): TTP = copy(items = items + (taxId -> data))

  def insertErrorData(taxId: TaxId, errors: List[EligibilityError]): TTP = copy(items = items + (taxId -> fromErrors(errors)))

  def fromErrors(errors: List[EligibilityError]): TtpEligibilityData = {
    val rlsOnAddress: Boolean = errors.contains(EligibilityError.RLSFlagIsSet)
    val markedAsInsolvent: Boolean = errors.contains(EligibilityError.PayeIsInsolvent)
    val minimumDebtAllowance: Boolean = false
    val maxDebtAllowance: Boolean = errors.contains(EligibilityError.DebtIsTooLarge)
    val disallowedChargeLock: Boolean = errors.contains(EligibilityError.PayeHasDisallowedCharges)
    val existingTTP: Boolean = errors.contains(EligibilityError.YouAlreadyHaveAPaymentPlan)
    val maxDebtAge: Boolean = errors.contains(EligibilityError.DebtIsTooOld)
    val eligibleChargeType: Boolean = errors.contains(EligibilityError.OutstandingPenalty)
    val returnsFiled: Boolean = errors.contains(EligibilityError.ReturnsAreNotUpToDate)

    def rules: EligibilityRules = EligibilityRules(
                                        rlsOnAddress = rlsOnAddress,
                                        rlsReason =  "",
                                        markedAsInsolvent =markedAsInsolvent,
                                        minimumDebtAllowance = minimumDebtAllowance,
                                        maxDebtAllowance = maxDebtAllowance,
                                        disallowedChargeLock = disallowedChargeLock,
                                        existingTTP = existingTTP,
                                        minInstalmentAmount = 300,
                                        maxInstalmentAmount = 600,
                                        maxDebtAge = maxDebtAge,
                                        eligibleChargeType = eligibleChargeType,
                                        returnsFiled = returnsFiled)
    TTP.IneligibleDefault.copy(eligibilityRules = rules)
  }

}

object TTP {

  val Empty: TTP = TTP(Map.empty)

  // language=JSON
  val jsString: String =
    """{
        "idType": "SSTTP",
        "idNumber": "A00000000001",
        "regimeType": "PAYE",
        "processingDate": "2022-03-10",
        "customerDetails": {
          "country": "NI",
          "postCode": "B5 7LN"
        },
        "eligibilityStatus": {
          "overallEligibilityStatus": false,
          "minPlanLengthMonths": 1,
          "maxPlanLengthMonths": 6
        },
        "eligibilityRules": {
          "rlsOnAddress": true,
          "rlsReason": "Receiver is not known",
          "markedAsInsolvent": false,
          "minimumDebtAllowance": false,
          "maxDebtAllowance": false,
          "disallowedChargeLock": false,
          "existingTTP": false,
          "minInstalmentAmount": 300,
          "maxInstalmentAmount": 600,
          "maxDebtAge": false,
          "eligibleChargeType": false,
          "returnsFiled": false
        },
        "financialLimitBreached": {
          "status": true,
          "calculatedAmount": 16000
        },
        "chargeTypeAssessment": [
          {
            "taxPeriodFrom": "2020-08-13",
            "taxPeriodTo": "2020-08-14",
            "debtTotalAmount": 300000,
            "taxPeriodCharges": [
              {
                "chargeId": "T5545454554",
                "mainTrans": "22000",
                "mainTransDesc": "",
                "subTrans": "1000",
                "subTransDesc": "",
                "outstandingDebtAmount": 100000,
                "interestStartDate": "2017-03-07",
                "accruedInterestToDate": 15.97,
                "disallowedCharge": true,
                "chargeLocks": {
                  "paymentLock": {
                    "status": false,
                    "reason": ""
                  },
                  "clearingLock": {
                    "status": false,
                    "reason": "there is no reason"
                  },
                  "interestLock": {
                    "status": false,
                    "reason": "there is no reason"
                  },
                  "dunningLock": {
                    "status": false,
                    "reason": "there is no reason"
                  },
                  "disallowedLock": {
                    "status": false,
                    "reason": "there is no reason"
                  }
                }
              }
            ]
          }
        ]
      }
      """

  lazy val Default = Json.parse(jsString).as[TtpEligibilityData]

  val IneligibleDefault =
    TtpEligibilityData(
      "SSTTP",
      "A00000000001",
      "PAYE",
      "2022-03-10",
      CustomerDetails("NI", "B5 7LN"),
      EligibilityStatus(false, 1, 6),
      EligibilityRules(false,"",false,false,false,false,false,300,600,false,false,false),
      FinancialLimitBreached(true, 16000),
      Nil
    )
}
