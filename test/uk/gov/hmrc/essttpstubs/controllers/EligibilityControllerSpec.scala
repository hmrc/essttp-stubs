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

import uk.gov.hmrc.essttpstubs.testutil.{ ItSpec, TestData }
import uk.gov.hmrc.http.{ HttpReads, HttpReadsInstances, HttpResponse, UpstreamErrorResponse }
import TestData.EligibilityApi.ModelInstances._
import uk.gov.hmrc.essttpstubs.model.EligibilityResponse

class EligibilityControllerSpec extends ItSpec {

  implicit val readResponse: HttpReads[HttpResponse] = HttpReadsInstances.throwOnFailure(HttpReadsInstances.readEitherOf(HttpReadsInstances.readRaw))

  "EligibilityController" - {

    ".insertEligibilityData() should return 'Created/201'" in {
      connector.insertEligibilityData(eligibilityResponse).futureValue.status shouldBe 201
    }

    ".retrieveEligibilityData should return correct EligibilityResponse" in {
      connector.insertEligibilityData(eligibilityResponse)
      val response: HttpResponse = connector.retrieveEligibilityData(eligibilityRequest).futureValue
      response.json.as[EligibilityResponse] shouldBe eligibilityResponse
    }

    ".retrieveEligibilityData should return 'NotFound/404' when no corresponding record found" in {
      val result = connector.retrieveEligibilityData(eligibilityRequest).failed.futureValue
      result.asInstanceOf[UpstreamErrorResponse].getMessage() should include("returned 404")
    }

    ".removeAllRecordsFromEligibilityDb return 'Accepted/202'" in {
      connector.removeAllRecordsFromEligibilityDb().futureValue.status shouldBe 202
    }

  }
}
