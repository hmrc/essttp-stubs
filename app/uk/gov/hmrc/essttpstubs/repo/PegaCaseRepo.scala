/*
 * Copyright 2025 HM Revenue & Customs
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
import com.mongodb.client.model.ReplaceOptions
import org.mongodb.scala.model.{Filters, IndexModel, IndexOptions, Indexes}
import org.mongodb.scala.result.UpdateResult
import org.mongodb.scala.SingleObservableFuture
import play.api.libs.json.{Format, JsValue, Json, OFormat}
import uk.gov.hmrc.essttpstubs.repo.PegaCaseRepo.PegaCaseEntry
import uk.gov.hmrc.mongo.MongoComponent
import uk.gov.hmrc.mongo.play.json.PlayMongoRepository
import uk.gov.hmrc.mongo.play.json.formats.MongoJavatimeFormats

import java.time.Instant
import scala.concurrent.duration.DurationInt
import java.util.concurrent.TimeUnit
import scala.concurrent.{ExecutionContext, Future}

@SuppressWarnings(Array("org.wartremover.warts.Any"))
@Singleton
class PegaCaseRepo @Inject() (
  mongoComponent: MongoComponent
)(using ExecutionContext)
    extends PlayMongoRepository[PegaCaseEntry](
      mongoComponent = mongoComponent,
      collectionName = "essttp-stubs-pega-get-case",
      domainFormat = PegaCaseEntry.format,
      indexes = PegaCaseRepo.indexes(30.minutes.toSeconds),
      replaceIndexes = true
    ) {

  def insert(pegaCaseEntry: PegaCaseEntry): Future[UpdateResult] =
    collection
      .replaceOne(
        Filters.equal("caseId", pegaCaseEntry.caseId),
        pegaCaseEntry,
        new ReplaceOptions().upsert(true)
      )
      .toFuture()

  def find(caseId: String): Future[Option[PegaCaseEntry]] =
    collection.find(Filters.equal("caseId", caseId)).headOption()

  def dropAll(): Future[Unit] =
    collection.drop().toFuture()

}

object PegaCaseRepo {

  final case class PegaCaseEntry(caseId: String, getCaseResponse: JsValue, createdAt: Instant)

  object PegaCaseEntry {

    @SuppressWarnings(Array("org.wartremover.warts.Any"))
    given format: OFormat[PegaCaseEntry] = {
      given Format[Instant] = MongoJavatimeFormats.instantFormat
      Json.format
    }
  }

  def indexes(cacheTtlInSeconds: Long): Seq[IndexModel] = Seq(
    IndexModel(
      keys = Indexes.ascending("caseId")
    ),
    IndexModel(
      keys = Indexes.ascending("createdAt"),
      indexOptions = IndexOptions().expireAfter(cacheTtlInSeconds, TimeUnit.SECONDS).name("createdAtIdx")
    )
  )

}
