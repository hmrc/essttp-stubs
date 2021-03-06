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

import essttp.rootmodel.ttp.EligibilityCheckResult
import play.api.Logger
import play.api.libs.json.{JsObject, Json}
import play.api.mvc.{Action, AnyContent, ControllerComponents}
import uk.gov.hmrc.essttpstubs.model.EligibilityRequest
import uk.gov.hmrc.essttpstubs.services.EligibilityService
import uk.gov.hmrc.essttpstubs.util.LoggingHelper
import uk.gov.hmrc.play.bootstrap.backend.controller.BackendController

import javax.inject.{Inject, Singleton}
import scala.concurrent.ExecutionContext

@Singleton()
class EligibilityController @Inject() (
    cc:                 ControllerComponents,
    eligibilityService: EligibilityService
)(implicit ec: ExecutionContext) extends BackendController(cc) {

  private val logger: Logger = Logger(this.getClass)

  def insertEligibilityData(): Action[JsObject] = Action.async(parse.json[JsObject]) { implicit request =>
    eligibilityService.insertEligibilityData(request.body.as[EligibilityCheckResult])
      .map(_ => Created(s"Inserted eligibility record into MongoDb: [ ${request.body.toString} ]"))
  }

  def retrieveEligibilityData: Action[EligibilityRequest] = Action.async(parse.json[EligibilityRequest]) { implicit request =>
    LoggingHelper.logRequestInfo(logger  = logger, request = request)
    for {
      eligibilityResponse: Option[EligibilityCheckResult] <- eligibilityService.eligibilityData(request.body)
    } yield eligibilityResponse match {
      case Some(validResponse) =>
        LoggingHelper.logResponseInfo(uri          = request.uri, logger = logger, responseBody = Json.toJson(validResponse))
        Ok(Json.toJson(validResponse))
      case None =>
        logger.info(s"No entry in mongo for eligibility request: [ ${request.body.toString} ]")
        NotFound //todo update this to reflect the spec responses maybe
    }
  }

  val removeAllRecordsFromEligibilityDb: Action[AnyContent] = Action.async { _ =>
    eligibilityService.removeAllRecordsFromEligibilityDb()
      .map(_ => Accepted("Removed all records from Eligibility MongoDb"))
  }

}
