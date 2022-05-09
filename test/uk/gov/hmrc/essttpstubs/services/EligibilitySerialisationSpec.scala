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

import play.api.libs.json.{ JsValue, Json }
import uk.gov.hmrc.essttpstubs.model.{ EligibilityRequest, EligibilityResponse }
import uk.gov.hmrc.essttpstubs.testutil.{ TestData, UnitSpec }

class EligibilitySerialisationSpec extends UnitSpec {

  "json de/serialize round trip" - {
    "EligibilityRequest" in {
      val json: JsValue = TestData.EligibilityApi.JsonInstances.eligibilityRequestJson
      val eligibilityRequest: EligibilityRequest = TestData.EligibilityApi.ModelInstances.eligibilityRequest
      Json.toJson(eligibilityRequest) shouldBe json
      json.as[EligibilityRequest] shouldBe eligibilityRequest
    }
    "EligibilityResponse" in {
      val json: JsValue = TestData.EligibilityApi.JsonInstances.eligibilityResponseJson
      val eligibilityResponse: EligibilityResponse = TestData.EligibilityApi.ModelInstances.eligibilityResponse
      Json.toJson(eligibilityResponse) shouldBe json
      json.as[EligibilityResponse] shouldBe eligibilityResponse
    }
  }
}
