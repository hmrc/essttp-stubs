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
import uk.gov.hmrc.essttpstubs.model.EligibilityRequest
import uk.gov.hmrc.http.{HeaderCarrier, HttpClient, HttpResponse}

import javax.inject.{Inject, Singleton}
import scala.concurrent.{ExecutionContext, Future}

@Singleton
class TestEligibilityConnector @Inject() (httpClient: HttpClient)(implicit executionContext: ExecutionContext) extends TestConnector {

  implicit val noOpCryptoFormat: CryptoFormat = CryptoFormat.NoOpCryptoFormat

  val eligibilityApiBaseUrl = s"http://localhost:$port/debts/time-to-pay/eligibility"

  def insertEligibilityData(eligibilityResponse: EligibilityCheckResult)(implicit hc: HeaderCarrier): Future[HttpResponse] =
    httpClient.POST[EligibilityCheckResult, HttpResponse](s"$eligibilityApiBaseUrl/insert", eligibilityResponse)

  def retrieveEligibilityData(eligibilityRequest: EligibilityRequest)(implicit hc: HeaderCarrier): Future[HttpResponse] =
    httpClient.POST[EligibilityRequest, HttpResponse](s"$eligibilityApiBaseUrl", eligibilityRequest)

  def removeAllRecordsFromEligibilityDb()(implicit hc: HeaderCarrier): Future[HttpResponse] =
    httpClient.DELETE[HttpResponse](s"$eligibilityApiBaseUrl/drop")

}
