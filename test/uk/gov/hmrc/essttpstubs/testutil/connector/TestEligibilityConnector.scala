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

package uk.gov.hmrc.essttpstubs.testutil.connector

import essttp.crypto.CryptoFormat
import essttp.rootmodel.ttp.eligibility.EligibilityCheckResult
import play.api.libs.json.Json
import play.api.libs.ws.JsonBodyWritables.writeableOf_JsValue
import uk.gov.hmrc.essttpstubs.model.EligibilityRequest
import uk.gov.hmrc.http.client.HttpClientV2
import uk.gov.hmrc.http.{HeaderCarrier, HttpResponse, StringContextOps}

import javax.inject.{Inject, Singleton}
import scala.concurrent.{ExecutionContext, Future}

@Singleton
class TestEligibilityConnector @Inject() (httpClient: HttpClientV2)(using ExecutionContext) extends TestConnector {

  given CryptoFormat = CryptoFormat.NoOpCryptoFormat

  val eligibilityApiBaseUrl = s"http://localhost:$port/debts/time-to-pay/eligibility"

  def insertEligibilityData(
    eligibilityResponse: EligibilityCheckResult
  )(using HeaderCarrier): Future[HttpResponse] =
    httpClient
      .post(url"$eligibilityApiBaseUrl/insert")
      .withBody(Json.toJson(eligibilityResponse))
      .execute[HttpResponse]

  def retrieveEligibilityData(
    eligibilityRequest: EligibilityRequest
  )(using HeaderCarrier): Future[HttpResponse] =
    httpClient
      .post(url"$eligibilityApiBaseUrl")
      .withBody(Json.toJson(eligibilityRequest))
      .execute[HttpResponse]

  def removeAllRecordsFromEligibilityDb()(using HeaderCarrier): Future[HttpResponse] =
    httpClient
      .delete(url"$eligibilityApiBaseUrl/drop")
      .execute[HttpResponse]

}
