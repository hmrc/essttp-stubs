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
import essttp.journey.model.ttp.EligibilityCheckResult
import org.mongodb.scala.model.{Filters, IndexModel, IndexOptions, Indexes}
import org.mongodb.scala.result.InsertOneResult
import play.api.libs.json._
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
  extends PlayMongoRepository[EligibilityCheckResult](
    mongoComponent = mongoComponent,
    collectionName = "essttp-stubs-eligibility",
    domainFormat   = EligibilityCheckResult.format,
    indexes        = EligibilityRepo.indexes(30.minutes.toSeconds),
    replaceIndexes = true
  ) {

  def insertEligibilityData(eligibilityCheckResult: EligibilityCheckResult): Future[InsertOneResult] =
    collection.insertOne(eligibilityCheckResult).toFuture()

  def findEligibilityDataByTaxRef(taxRef: String): Future[Option[EligibilityCheckResult]] =
    collection.find(Filters.eq("identification.idValue", taxRef)).headOption()

  def removeAllRecords(): Future[Unit] = collection.drop().toFuture().map(_ => ())

}

object EligibilityRepo {
  implicit val format: OFormat[JsObject] = new OFormat[JsObject] {
    override def reads(json: JsValue): JsResult[JsObject] = json match {
      case jsObject: JsObject => JsSuccess(jsObject)
      case _                  => JsError("Not json")
    }

    override def writes(o: JsObject): JsObject = o
  }

  def indexes(cacheTtlInSeconds: Long): Seq[IndexModel] = Seq(
    IndexModel(
      keys         = Indexes.ascending("lastUpdated"),
      indexOptions = IndexOptions().expireAfter(cacheTtlInSeconds, TimeUnit.SECONDS).name("lastUpdatedIdx")
    )
  )
}
