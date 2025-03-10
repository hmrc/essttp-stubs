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

import play.api.libs.json.{JsValue, Json}
import play.api.libs.ws.JsonBodyWritables.writeableOf_JsValue
import uk.gov.hmrc.http.client.HttpClientV2
import uk.gov.hmrc.http.{HeaderCarrier, HttpResponse, StringContextOps}

import javax.inject.{Inject, Singleton}
import scala.concurrent.{ExecutionContext, Future}

@Singleton
class TestAffordabilityConnector @Inject() (httpClient: HttpClientV2)(using ExecutionContext) extends TestConnector {

  private val affordabilityApiUrl = s"http://localhost:$port/debts/time-to-pay/self-serve/affordability"

  def calculateInstalmentAmounts(request: JsValue)(implicit hc: HeaderCarrier): Future[HttpResponse] =
    httpClient
      .post(url"$affordabilityApiUrl")
      .withBody(Json.toJson(request))
      .execute[HttpResponse]

}
