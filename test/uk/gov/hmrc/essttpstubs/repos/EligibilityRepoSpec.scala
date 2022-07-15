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

package uk.gov.hmrc.essttpstubs.repos

import play.api.libs.json.Json
import uk.gov.hmrc.essttpstubs.testutil.{ItSpec, TestData}

class EligibilityRepoSpec extends ItSpec {

  "insert a record into mongodb" in {
    collectionSize shouldBe 0

    val dbOperation = eligibilityRepo.upsert(
      id = TestData.EligibilityApi.ModelInstances.eligibilityResponse.identification(0).idValue.value,
      a  = Json.toJsObject(TestData.EligibilityApi.ModelInstances.eligibilityResponse)
    ).futureValue
    dbOperation.n shouldBe 1
    dbOperation.ok shouldBe true

    collectionSize shouldBe 1

    val x = eligibilityRepo.findEligibilityDataByTaxRef(TestData.EligibilityApi.ModelInstances.eligibilityResponse.identification(0).idValue.value)
      .futureValue.value
    x shouldBe TestData.EligibilityApi.JsonInstances.eligibilityResponseJson withClue (s"Json was infact: ${x}")
  }

  "drop the records from mongodb" in {
    collectionSize shouldBe 0

    val dbOperationInsert = eligibilityRepo.upsert(
      id = TestData.EligibilityApi.ModelInstances.eligibilityResponse.identification(0).idValue.value,
      a  = Json.toJsObject(TestData.EligibilityApi.ModelInstances.eligibilityResponse)
    ).futureValue
    dbOperationInsert.n shouldBe 1
    dbOperationInsert.ok shouldBe true
    collectionSize shouldBe 1

    val dbOperationRemove = eligibilityRepo.removeAllRecords().futureValue
    dbOperationRemove.n shouldBe 1
    dbOperationRemove.ok shouldBe true
    collectionSize shouldBe 0
  }

  private def collectionSize: Int = eligibilityRepo.count(Json.obj()).futureValue
}
