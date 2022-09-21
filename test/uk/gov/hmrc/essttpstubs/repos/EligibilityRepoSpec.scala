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

import essttp.crypto.CryptoFormat
import org.mongodb.scala.result.InsertOneResult
import play.api.libs.json.Json
import uk.gov.hmrc.essttpstubs.repo.EligibilityEntry
import uk.gov.hmrc.essttpstubs.testutil.{ItSpec, TestData}

import java.time.Instant

class EligibilityRepoSpec extends ItSpec {

  implicit val noOpCryptoFormat: CryptoFormat = CryptoFormat.NoOpCryptoFormat

  "insert a record into mongodb" in {
    collectionSize shouldBe 0

    val dbOperation: InsertOneResult = eligibilityRepo
      .insertEligibilityData(EligibilityEntry(TestData.EligibilityApi.ModelInstances.eligibilityResponse, Instant.now()))
      .futureValue

    dbOperation.wasAcknowledged() shouldBe true
    collectionSize shouldBe 1

    val findResult: Option[EligibilityEntry] = eligibilityRepo
      .findEligibilityDataByTaxRef(TestData.EligibilityApi.ModelInstances.eligibilityResponse.identification(0).idValue.value)
      .futureValue

    Json.toJson(findResult.value.eligibilityCheckResult) shouldBe TestData.EligibilityApi.JsonInstances.eligibilityResponseJson withClue s"Json was infact: $findResult"
  }

  "drop the records from mongodb" in {
    collectionSize shouldBe 0

    val dbOperationInsert: InsertOneResult = eligibilityRepo
      .insertEligibilityData(EligibilityEntry(TestData.EligibilityApi.ModelInstances.eligibilityResponse, Instant.now()))
      .futureValue

    dbOperationInsert.wasAcknowledged() shouldBe true
    collectionSize shouldBe 1

    eligibilityRepo.removeAllRecords().futureValue
    collectionSize shouldBe 0
  }

  private def collectionSize: Int = eligibilityRepo.collection.find().toFuture().map(_.toList.size).futureValue
}
