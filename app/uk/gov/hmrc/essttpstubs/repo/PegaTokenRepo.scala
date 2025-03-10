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

package uk.gov.hmrc.essttpstubs.repo

import com.google.inject.{Inject, Singleton}
import org.mongodb.scala.model.{IndexModel, IndexOptions, Indexes}
import org.mongodb.scala.result.InsertOneResult
import org.mongodb.scala.SingleObservableFuture
import uk.gov.hmrc.essttpstubs.config.AppConfig
import uk.gov.hmrc.essttpstubs.model.PegaOauthToken
import uk.gov.hmrc.mongo.MongoComponent
import uk.gov.hmrc.mongo.play.json.PlayMongoRepository

import java.util.concurrent.TimeUnit
import scala.concurrent.{ExecutionContext, Future}

@SuppressWarnings(Array("org.wartremover.warts.Any"))
@Singleton
final class PegaTokenRepo @Inject() (
  mongoComponent: MongoComponent,
  appConfig:      AppConfig
)(using ExecutionContext)
    extends PlayMongoRepository[PegaOauthToken](
      mongoComponent = mongoComponent,
      collectionName = "essttp-stubs-pega-token",
      domainFormat = PegaOauthToken.pegaFormat,
      indexes = PegaTokenRepo.indexes(appConfig.pegaTokenExpiryTime.toSeconds * 2),
      replaceIndexes = true
    ) {

  def insertPegaToken(pegaToken: PegaOauthToken): Future[InsertOneResult] =
    collection
      .drop()
      .toFuture()
      .flatMap { _ =>
        collection.insertOne(pegaToken).toFuture()
      }

  def findPegaToken(): Future[Option[PegaOauthToken]] =
    collection.find().headOption()

  def dropAll(): Future[Unit] =
    collection.drop().toFuture()

}

object PegaTokenRepo {

  def indexes(cacheTtlInSeconds: Long): Seq[IndexModel] = Seq(
    IndexModel(
      keys = Indexes.ascending("createdAt"),
      indexOptions = IndexOptions().expireAfter(cacheTtlInSeconds, TimeUnit.SECONDS).name("createdAtIdx")
    )
  )
}
