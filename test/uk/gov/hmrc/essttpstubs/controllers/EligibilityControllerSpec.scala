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

import essttp.crypto.CryptoFormat.NoOpCryptoFormat
import essttp.rootmodel.ttp.eligibility.{EligibilityCheckResult, IdType, IdValue, Identification}
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
      response.json shouldBe eligibilityResponseJson withClue s"Json was infact: ${response.json.toString}"
    }

    ".retrieveEligibilityData should return a default random EligibilityResponse if none were found in  mongo" in {
      val response: HttpResponse = testEligibilityConnector.retrieveEligibilityData(eligibilityRequest.copy(identification = List(Identification(IdType("EMPREF"), IdValue("iwontexist"))))).futureValue
      response.json.asOpt[EligibilityCheckResult](EligibilityCheckResult.format(NoOpCryptoFormat)).isDefined shouldBe true withClue "No EligibilityCheckResult"
    }

    ".retrieveEligibilityData should return 'NotFound/404' when idValue is 'NotFound' - just for not found scenario" in {
      val result = testEligibilityConnector.retrieveEligibilityData(eligibilityRequest.copy(identification = List(Identification(IdType("EMPREF"), IdValue("NotFound"))))).failed.futureValue
      asUpstreamErrorResponse(result).getMessage() should include("returned 404")
    }

    ".removeAllRecordsFromEligibilityDb return 'Accepted/202'" in {
      testEligibilityConnector.removeAllRecordsFromEligibilityDb().futureValue.status shouldBe 202
    }

  }
}
