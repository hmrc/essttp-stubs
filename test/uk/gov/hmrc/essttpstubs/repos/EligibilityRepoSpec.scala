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

package uk.gov.hmrc.essttpstubs.repos

import essttp.crypto.CryptoFormat
import essttp.rootmodel.ttp.eligibility.EligibilityCheckResult
import org.mongodb.scala.result.UpdateResult
import org.mongodb.scala.ObservableFuture
import play.api.libs.json.Json
import uk.gov.hmrc.essttpstubs.repo.EligibilityEntry
import uk.gov.hmrc.essttpstubs.testutil.Givens.canEqualJsValue
import uk.gov.hmrc.essttpstubs.testutil.{ItSpec, TestData}

import java.time.Instant

class EligibilityRepoSpec extends ItSpec {

  given CryptoFormat = CryptoFormat.NoOpCryptoFormat

  "insert a record into mongodb" in {
    collectionSize shouldBe 0

    val updateResult1: UpdateResult = eligibilityRepo
      .insertEligibilityData(
        EligibilityEntry(TestData.EligibilityApi.ModelInstances.eligibilityResponse, Instant.now())
      )
      .futureValue

    updateResult1.wasAcknowledged() shouldBe true
    collectionSize shouldBe 1

    // check multiple inserts for same tax id doesn't generate multiple records
    val updateResult2: UpdateResult = eligibilityRepo
      .insertEligibilityData(
        EligibilityEntry(TestData.EligibilityApi.ModelInstances.eligibilityResponse, Instant.now())
      )
      .futureValue

    updateResult2.wasAcknowledged() shouldBe true
    collectionSize shouldBe 1

    val identificationList = TestData.EligibilityApi.ModelInstances.eligibilityResponse.identification

    val findResult: Option[EligibilityEntry] = eligibilityRepo
      .findEligibilityDataByTaxRef(identificationList)
      .futureValue

    Json
      .toJson(
        findResult.value.eligibilityCheckResult
      )
      .as[EligibilityCheckResult] shouldBe TestData.EligibilityApi.JsonInstances.eligibilityResponseJson
      .as[EligibilityCheckResult]
  }

  "drop the records from mongodb" in {
    collectionSize shouldBe 0

    val dbOperationInsert: UpdateResult = eligibilityRepo
      .insertEligibilityData(
        EligibilityEntry(TestData.EligibilityApi.ModelInstances.eligibilityResponse, Instant.now())
      )
      .futureValue

    dbOperationInsert.wasAcknowledged() shouldBe true
    collectionSize shouldBe 1

    eligibilityRepo.removeAllRecords().futureValue
    collectionSize shouldBe 0
  }

  private def collectionSize: Int = eligibilityRepo.collection.find().toFuture().map(_.toList.size).futureValue
}
