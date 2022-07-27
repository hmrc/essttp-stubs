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

package uk.gov.hmrc.essttpstubs.repo

import com.google.inject.{Inject, Singleton}
import org.mongodb.scala.model.{Filters, IndexModel, IndexOptions, Indexes}
import org.mongodb.scala.result.InsertOneResult
import uk.gov.hmrc.essttpstubs.config.EligibilityRepoConfig
import uk.gov.hmrc.mongo.MongoComponent
import uk.gov.hmrc.mongo.play.json.PlayMongoRepository

import java.util.concurrent.TimeUnit
import scala.concurrent.duration.DurationInt
import scala.concurrent.{ExecutionContext, Future}

@SuppressWarnings(Array("org.wartremover.warts.Any"))
@Singleton
final class EligibilityRepo @Inject() (
    mongoComponent:        MongoComponent,
    eligibilityRepoConfig: EligibilityRepoConfig
)(implicit ec: ExecutionContext)
  extends PlayMongoRepository[EligibilityEntry](
    mongoComponent = mongoComponent,
    collectionName = "essttp-stubs-eligibility",
    domainFormat   = EligibilityEntry.format,
    indexes        = EligibilityRepo.indexes(30.minutes.toSeconds),
    replaceIndexes = true
  ) {

  def insertEligibilityData(eligibilityEntry: EligibilityEntry): Future[InsertOneResult] =
    collection.insertOne(eligibilityEntry).toFuture()

  def findEligibilityDataByTaxRef(taxRef: String): Future[Option[EligibilityEntry]] =
    collection.find(Filters.eq("eligibilityCheckResult.identification.idValue", taxRef)).headOption()

  def removeAllRecords(): Future[Unit] = collection.drop().toFuture().map(_ => ())

}

object EligibilityRepo {

  def indexes(cacheTtlInSeconds: Long): Seq[IndexModel] = Seq(
    IndexModel(
      keys         = Indexes.ascending("createdAt"),
      indexOptions = IndexOptions().expireAfter(cacheTtlInSeconds, TimeUnit.SECONDS)
    )
  )
}
