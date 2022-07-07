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

package uk.gov.hmrc.essttpstubs.controllers

import essttp.journey.model.ttp.{CustomerReference, ProcessingDate}
import essttp.journey.model.ttp.arrangement.ArrangementResponse
import play.api.http.Status.BAD_REQUEST
import play.api.libs.json.{JsSuccess, Json}
import uk.gov.hmrc.essttpstubs.testutil.ItSpec
import uk.gov.hmrc.essttpstubs.testutil.connector.TestArrangementConnector

class ArrangementControllerSpec extends ItSpec {

  lazy val testArrangementConnector: TestArrangementConnector = injector.instanceOf[TestArrangementConnector]

  "ArrangementController" - {

    ".enactArrangement should return BadRequest when no ID with ID type BROCS can be found in the request" in {
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

    ".enactArrangement should return Ok when an ID with ID type BROCS can be found in the request" in {
      val request = Json.parse(
        """{
          |  "identification" : [
          |    {
          |      "idType": "BROCS",
          |      "idValue": "id"
          |    }
          |  ]
          |}
          |""".stripMargin
      )

      val response = testArrangementConnector.enactArrangement(request).futureValue
      response.json.validate[ArrangementResponse] shouldBe JsSuccess(
        ArrangementResponse(
          ProcessingDate("2057-08-02T15:28:55.185Z"),
          CustomerReference("id")
        )
      )
    }

  }

}
