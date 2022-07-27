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

package uk.gov.hmrc.essttpstubs.testutil.connector

import play.api.libs.json.JsValue
import uk.gov.hmrc.http.{HeaderCarrier, HttpClient, HttpResponse}

import javax.inject.Inject
import scala.concurrent.{ExecutionContext, Future}

class TestArrangementConnector @Inject() (httpClient: HttpClient)(implicit executionContext: ExecutionContext) extends TestConnector {

  private val arrangementApiUrl = s"http://localhost:$port/debts/time-to-pay/self-serve/arrangement"

  def enactArrangement(request: JsValue)(implicit hc: HeaderCarrier): Future[HttpResponse] =
    httpClient.POST[JsValue, HttpResponse](arrangementApiUrl, request)

}
