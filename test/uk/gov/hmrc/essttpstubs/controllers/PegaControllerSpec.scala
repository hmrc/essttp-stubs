/*
 * Copyright 2024 HM Revenue & Customs
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

import play.api.libs.json.{JsObject, JsSuccess, Json}
import play.api.test.FakeRequest
import play.api.test.Helpers._
import uk.gov.hmrc.essttpstubs.testutil.ItSpec

class PegaControllerSpec extends ItSpec {

  val controller = app.injector.instanceOf[PegaController]

  "PegaController" - {

    "when handling requests to the token endpoint must" - {

      "respond with a token" in {
        val result = controller.token(FakeRequest())
        val json = contentAsJson(result).as[JsObject]

        (json \ "token_type").as[String] shouldBe "bearer"
        (json \ "expires_in").as[Long] shouldBe 360000
        (json \ "access_token").validate[String] shouldBe a[JsSuccess[_]]
      }

    }

    "when handling requests to start a case must" - {

      "respond successfully" in {
        val result = controller.startCase(FakeRequest())
        val json = contentAsJson(result).as[JsObject]

        (json \ "ID").validate[String] shouldBe a[JsSuccess[_]]
        val assignments = (json \ "data" \ "caseInfo" \ "assignments").as[Seq[JsObject]]
        assignments.map(a => (a \ "ID").validate[String]).foreach(_ shouldBe a[JsSuccess[_]])
      }

    }

    "when handling requests to get a case must" - {

      "respond successfully" in {
        val expectedJson =
          Json.parse(
            """
              |{
              |  "paymentPlan": {
              |    "numberOfInstalments": 4,
              |    "planDuration": 4,
              |    "totalDebt": 997700,
              |    "totalDebtIncInt": 997816,
              |    "planInterest": 116,
              |    "collections": {
              |      "initialCollection": {
              |        "dueDate": "2024-08-27",
              |        "amountDue": 2300
              |      },
              |      "regularCollections": [
              |        {
              |          "dueDate": "2024-10-28",
              |          "amountDue": 249454
              |        },
              |        {
              |          "dueDate": "2024-11-28",
              |          "amountDue": 249454
              |        },
              |        {
              |          "dueDate": "2024-12-28",
              |          "amountDue": 249454
              |        },
              |        {
              |          "dueDate": "2025-01-28",
              |          "amountDue": 249454
              |        }
              |      ]
              |    },
              |    "instalments": [
              |      {
              |        "instalmentNumber": 4,
              |        "dueDate": "2025-01-28",
              |        "instalmentInterestAccrued": 29,
              |        "instalmentBalance": 249425,
              |        "debtItemChargeId": "A00000000001",
              |        "amountDue": 249425,
              |        "debtItemOriginalDueDate": "2023-09-28"
              |      },
              |      {
              |        "instalmentNumber": 3,
              |        "dueDate": "2024-12-28",
              |        "instalmentInterestAccrued": 29,
              |        "instalmentBalance": 498850,
              |        "debtItemChargeId": "A00000000001",
              |        "amountDue": 249425,
              |        "debtItemOriginalDueDate": "2023-09-28"
              |      },
              |      {
              |        "instalmentNumber": 2,
              |        "dueDate": "2024-11-28",
              |        "instalmentInterestAccrued": 29,
              |        "instalmentBalance": 748275,
              |        "debtItemChargeId": "A00000000001",
              |        "amountDue": 249425,
              |        "debtItemOriginalDueDate": "2023-09-28"
              |      },
              |      {
              |        "instalmentNumber": 1,
              |        "dueDate": "2024-10-28",
              |        "instalmentInterestAccrued": 29,
              |        "instalmentBalance": 997700,
              |        "debtItemChargeId": "A00000000001",
              |        "amountDue": 249425,
              |        "debtItemOriginalDueDate": "2023-09-28"
              |      }
              |    ]
              |  }
              |}
              |""".stripMargin
          )

        val result = controller.getCase("case")(FakeRequest())
        contentAsJson(result) shouldBe expectedJson
      }

    }

  }

}

