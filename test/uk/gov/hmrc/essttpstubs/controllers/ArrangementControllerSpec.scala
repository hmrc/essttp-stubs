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

package uk.gov.hmrc.essttpstubs.controllers

import essttp.rootmodel.ttp.arrangement.{ArrangementResponse, CustomerReference}
import essttp.rootmodel.ttp.eligibility.ProcessingDateTime
import play.api.http.Status.BAD_REQUEST
import play.api.libs.json.{JsSuccess, Json}
import uk.gov.hmrc.essttpstubs.testutil.ItSpec
import uk.gov.hmrc.essttpstubs.testutil.connector.TestArrangementConnector

class ArrangementControllerSpec extends ItSpec {

  lazy val testArrangementConnector: TestArrangementConnector = injector.instanceOf[TestArrangementConnector]

  "ArrangementController" - {

    ".enactArrangement should return BadRequest when no ID type can be found in the request" in {
      val request = Json.parse(
        """{
            |  "identification" : [
            |  ]
            |}
            |""".stripMargin
      )

      val response = testArrangementConnector.enactArrangement(request).failed.futureValue
      asUpstreamErrorResponse(response).statusCode shouldBe BAD_REQUEST
    }

    Seq(
      ("PAYE", "BROCS"),
      ("VATC", "VRN"),
      ("SA", "UTR")
    ).foreach {
        case (regimeType, identificationKey) =>
          s".enactArrangement should return Ok when an ID with ID type $identificationKey can be found for the regimeType $regimeType in the request" in {
            val request = Json.parse(
              s"""
            |{
            |	"identification": [{
            |		"idType": "$identificationKey",
            |		"idValue": "id"
            |	}],
            |	"channelIdentifier": "eSSTTP",
            |	"regimeType": "$regimeType",
            |	"regimePaymentFrequency": "Monthly",
            |	"arrangementAgreedDate": "2022-06-08",
            |	"directDebitInstruction": {
            |		"sortCode": "123456",
            |		"accountNumber": "12345678",
            |		"accountName": "Current",
            |		"paperAuddisFlag": false
            |	},
            |	"paymentPlan": {
            |		"planDuration": 2,
            |		"totalDebt": 745183,
            |		"totalDebtIncInt": 752238,
            |		"planInterest": 5755,
            |		"paymentPlanFrequency": "Monthly",
            |		"collections": {
            |			"initialCollection": {
            |				"dueDate": "2022-06-18",
            |				"amountDue": 1000
            |			},
            |			"regularCollections": [{
            |				"dueDate": "2022-07-08",
            |				"amountDue": 130447
            |			}, {
            |				"dueDate": "2022-08-08",
            |				"amountDue": 130447
            |			}]
            |		},
            |		"numberOfInstalments": 2,
            |		"instalments": [{
            |			"instalmentNumber": 1,
            |			"dueDate": "2022-06-18",
            |			"instalmentInterestAccrued": 459,
            |			"instalmentBalance": 480896,
            |			"debtItemChargeId": "XW006559808862",
            |			"amountDue": 1000,
            |			"debtItemOriginalDueDate": "2022-05-22"
            |		}, {
            |			"instalmentNumber": 2,
            |			"dueDate": "2022-06-18",
            |			"instalmentInterestAccrued": 459,
            |			"instalmentBalance": 480896,
            |			"debtItemChargeId": "XW006559808862",
            |			"amountDue": 1000,
            |			"debtItemOriginalDueDate": "2022-05-22"
            |		}],
            |   "debtItemCharges": [{
            |     "outstandingDebtAmount": 148781,
            |     "debtItemChargeId": "XW006559808862",
            |     "debtItemOriginalDueDate": "2022-05-22",
            |     "accruedInterest": 3
            |   }, {
            |     "outstandingDebtAmount": 148781,
            |     "debtItemChargeId": "XW006559808862",
            |     "debtItemOriginalDueDate": "2022-05-22",
            |     "accruedInterest": 3
            |	  }]
            |	}
            |}
            |""".stripMargin
            )

            val response = testArrangementConnector.enactArrangement(request).futureValue
            response.json.validate[ArrangementResponse] shouldBe JsSuccess(
              ArrangementResponse(
                ProcessingDateTime("2057-08-02T15:28:55.185Z"),
                CustomerReference("id")
              )
            )
          }
      }

  }

}
