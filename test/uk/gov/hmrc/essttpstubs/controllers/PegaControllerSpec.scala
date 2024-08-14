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

import play.api.libs.json.{JsObject, JsSuccess}
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

  }

}

