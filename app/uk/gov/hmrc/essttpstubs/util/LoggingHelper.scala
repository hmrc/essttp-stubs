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

package uk.gov.hmrc.essttpstubs.util

import play.api.Logger
import play.api.libs.json.{Format, JsValue, Json}
import play.api.mvc.Request

object LoggingHelper {

  def logRequestInfo[A](infoMessage: String = "Request info for: ", logger: Logger, request: Request[A])(implicit format: Format[A]): Unit =
    logger.info(
      s"$infoMessage " +
        s"[ uri: ${request.uri} ] " +
        s"[ headers:  ${request.headers} ]" +
        s"[body: ${Json.prettyPrint(Json.toJson(request.body)(format.writes))} ]"
    )

  def logResponseInfo(uri: String, logger: Logger, responseBody: JsValue): Unit =
    logger.info(s"Response body for request to $uri: [ ${Json.prettyPrint(Json.toJson(responseBody))} ]")

}
