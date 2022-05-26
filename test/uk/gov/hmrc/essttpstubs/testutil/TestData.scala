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

package uk.gov.hmrc.essttpstubs.testutil

import play.api.libs.json.{ JsValue, Json }
import uk.gov.hmrc.essttpstubs.model.{ ChargeLock, ChargeLocks, ChargeTypeAssessment, CustomerPostcode, DisallowedChargeLocks, EligibilityRequest, EligibilityResponse, EligibilityRules, EligibilityStatus }

object TestData {

  object EligibilityApi {

    object ModelInstances {

      val testChargeLock: ChargeLock = ChargeLock(status = false, reason = "some reason")

      val eligibilityRequest: EligibilityRequest = EligibilityRequest(
        idType = "test-idType",
        idNumber = "test-idNumber",
        regimeType = "test-regimeType",
        returnFinancials = true)

      val eligibilityResponse: EligibilityResponse = EligibilityResponse(
        idType = "test-idType",
        idNumber = "test-idNumber",
        regimeType = "test-regimeType",
        processingDate = "test-processingDate",
        customerPostcodes = List(
          CustomerPostcode(addressPostcode = "test-postcode", postcodeDate = "2022-01-01")),
        minPlanLengthMonths = 1,
        maxPlanLengthMonths = 3,
        eligibilityStatus = EligibilityStatus(eligibilityPass = false),
        eligibilityRules = EligibilityRules(
          hasRlsOnAddress = true,
          markedAsInsolvent = true,
          isLessThanMinDebtAllowance = false,
          isMoreThanMaxDebtAllowance = false,
          disallowedChargeLocks = false,
          existingTTP = false,
          exceedsMaxDebtAge = false,
          eligibleChargeType = false,
          missingFiledReturns = false),
        chargeTypeAssessment = Seq(
          ChargeTypeAssessment(
            taxPeriodFrom = "2022-04-27",
            taxPeriodTo = "2022-04-27",
            debtTotalAmount = 100,
            disallowedChargeLocks = Seq(
              DisallowedChargeLocks(
                chargeId = "test-chargeId",
                mainTrans = "test-mainTrans",
                mainTransDesc = "test-mainTransDesc",
                subTrans = "test-subTrans",
                subTransDesc = "test-subTransDesc",
                outstandingDebtAmount = 10,
                interestStartDate = "2022-04-27",
                accruedInterestToDate = 1,
                chargeLocks = ChargeLocks(
                  paymentLock = testChargeLock,
                  clearingLock = testChargeLock,
                  interestLock = testChargeLock,
                  dunningLock = testChargeLock))))))
    }
    object JsonInstances {
      val eligibilityRequestJson: JsValue = Json.parse(
        //language=JSON
        """{
          | "idType": "test-idType",
          | "idNumber": "test-idNumber",
          | "regimeType": "test-regimeType",
          | "returnFinancials": true
          |}
          |""".stripMargin)
      val eligibilityResponseJson: JsValue = Json.parse(
        //language=JSON
        """{
          | "_id": "test-idNumber",
          |	"idType": "test-idType",
          |	"idNumber": "test-idNumber",
          |	"regimeType": "test-regimeType",
          |	"processingDate": "test-processingDate",
          |	"customerPostcodes": [
          |  {
          |	  "addressPostcode": "test-postcode",
          |		"postcodeDate": "2022-01-01"
          |  }
          | ],
          |	"minPlanLengthMonths": 1,
          |	"maxPlanLengthMonths": 3,
          |	"eligibilityStatus": {
          |		"eligibilityPass": false
          |	},
          |	"eligibilityRules": {
          |		"hasRlsOnAddress": true,
          |		"markedAsInsolvent": true,
          |		"isLessThanMinDebtAllowance": false,
          |		"isMoreThanMaxDebtAllowance": false,
          |		"disallowedChargeLocks": false,
          |		"existingTTP": false,
          |		"exceedsMaxDebtAge": false,
          |		"eligibleChargeType": false,
          |		"missingFiledReturns": false
          |	},
          |	"chargeTypeAssessment": [{
          |		"taxPeriodFrom": "2022-04-27",
          |		"taxPeriodTo": "2022-04-27",
          |		"debtTotalAmount": 100,
          |		"disallowedChargeLocks": [{
          |			"chargeId": "test-chargeId",
          |			"mainTrans": "test-mainTrans",
          |			"mainTransDesc": "test-mainTransDesc",
          |			"subTrans": "test-subTrans",
          |			"subTransDesc": "test-subTransDesc",
          |			"outstandingDebtAmount": 10,
          |			"interestStartDate": "2022-04-27",
          |			"accruedInterestToDate": 1,
          |			"chargeLocks": {
          |				"paymentLock": {
          |					"status": false,
          |					"reason": "some reason"
          |				},
          |				"clearingLock": {
          |					"status": false,
          |					"reason": "some reason"
          |				},
          |				"interestLock": {
          |					"status": false,
          |					"reason": "some reason"
          |				},
          |				"dunningLock": {
          |					"status": false,
          |					"reason": "some reason"
          |				}
          |			}
          |		}]
          |	}]
          |}""".stripMargin)
    }
  }

}
