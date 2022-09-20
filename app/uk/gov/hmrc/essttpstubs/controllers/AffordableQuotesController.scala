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

package uk.gov.hmrc.essttpstubs.controllers

import essttp.crypto.CryptoFormat
import essttp.rootmodel.ttp.affordablequotes.AffordableQuotesRequest
import play.api.Logger
import play.api.libs.json.Json
import play.api.mvc.{Action, ControllerComponents}
import uk.gov.hmrc.essttpstubs.services.AffordableQuotesService
import uk.gov.hmrc.essttpstubs.util.LoggingHelper
import uk.gov.hmrc.play.bootstrap.backend.controller.BackendController

import javax.inject.{Inject, Singleton}

@Singleton()
class AffordableQuotesController @Inject() (
    affordableQuotesService: AffordableQuotesService,
    cc:                      ControllerComponents
) extends BackendController(cc) {

  val logger: Logger = Logger(this.getClass)

  implicit val noOpCryptoFormat: CryptoFormat = CryptoFormat.NoOpCryptoFormat

  val affordableQuotes: Action[AffordableQuotesRequest] = Action(parse.json[AffordableQuotesRequest]) { implicit request =>
    LoggingHelper.logRequestInfo(logger  = logger, request = request)
    val response = affordableQuotesService.calculateAffordableQuotes(request.body)
    LoggingHelper.logResponseInfo(uri          = request.uri, logger = logger, responseBody = Json.toJson(response))
    Ok(Json.toJson(response))
  }
}
