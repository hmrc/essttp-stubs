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

import uk.gov.hmrc.essttpstubs.testutil.ItSpec
import uk.gov.hmrc.essttpstubs.testutil.TestData.EligibilityApi.JsonInstances._
import uk.gov.hmrc.essttpstubs.testutil.TestData.EligibilityApi.ModelInstances._
import uk.gov.hmrc.http.HttpResponse

class EligibilityControllerSpec extends ItSpec {

  "EligibilityController" - {

    ".insertEligibilityData() should return 'Created/201'" in {
      testEligibilityConnector.insertEligibilityData(eligibilityResponse).futureValue.status shouldBe 201
    }

    ".retrieveEligibilityData should return correct EligibilityResponse" in {
      testEligibilityConnector.insertEligibilityData(eligibilityResponse).futureValue.status shouldBe 201
      val response: HttpResponse = testEligibilityConnector.retrieveEligibilityData(eligibilityRequest).futureValue
      response.json shouldBe eligibilityResponseJson
    }

    ".retrieveEligibilityData should return 'NotFound/404' when no corresponding record found" in {
      val result = testEligibilityConnector.retrieveEligibilityData(eligibilityRequest).failed.futureValue
      asUpstreamErrorResponse(result).getMessage() should include("returned 404")
    }

    ".removeAllRecordsFromEligibilityDb return 'Accepted/202'" in {
      testEligibilityConnector.removeAllRecordsFromEligibilityDb().futureValue.status shouldBe 202
    }

  }
}
