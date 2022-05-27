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

import play.api.libs.json.{ JsError, JsObject, JsResult, JsSuccess, JsValue, OFormat }
import play.modules.reactivemongo.ReactiveMongoComponent
import reactivemongo.api.commands.WriteResult
import reactivemongo.api.indexes.{ Index, IndexType }
import reactivemongo.bson.BSONDocument
import uk.gov.hmrc.essttpstubs.config.EligibilityRepoConfig
import uk.gov.hmrc.essttpstubs.repo.EligibilityRepo.format

import javax.inject.{ Inject, Singleton }
import scala.concurrent.{ ExecutionContext, Future }

@Singleton
final class EligibilityRepo @Inject() (
  reactiveMongoComponent: ReactiveMongoComponent,
  eligibilityRepoConfig: EligibilityRepoConfig)(implicit ec: ExecutionContext)
  extends Repo[JsObject, String]("essttp-stubs-eligibility", reactiveMongoComponent) {

  override def indexes: Seq[Index] = Seq(
    Index(
      key = Seq("lastUpdated" -> IndexType.Ascending),
      name = Some("lastUpdatedId"),
      options = BSONDocument("expireAfterSeconds" -> eligibilityRepoConfig.expireEligibilityMongo.toSeconds)))

  def findEligibilityDataByTaxRef(taxRef: String): Future[Option[JsObject]] =
    find("idNumber" -> taxRef).map { records => records.headOption }

  def removeAllRecords(): Future[WriteResult] = removeAll()

}

object EligibilityRepo {
  implicit val format: OFormat[JsObject] = new OFormat[JsObject] {
    override def reads(json: JsValue): JsResult[JsObject] = json match {
      case jsObject: JsObject => JsSuccess(jsObject)
      case _ => JsError("Not json")
    }

    override def writes(o: JsObject): JsObject = o
  }
}