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

package uk.gov.hmrc.essttpstubs.controllers

import essttp.crypto.CryptoFormat
import essttp.rootmodel.ttp.eligibility.EligibilityCheckResult
import play.api.Logger
import play.api.libs.json.{JsObject, Json}
import play.api.mvc.{Action, AnyContent, ControllerComponents}
import uk.gov.hmrc.essttpstubs.model.EligibilityRequest
import uk.gov.hmrc.essttpstubs.services.EligibilityService
import uk.gov.hmrc.essttpstubs.util.LoggingHelper
import uk.gov.hmrc.play.bootstrap.backend.controller.BackendController

import javax.inject.{Inject, Singleton}
import scala.concurrent.{ExecutionContext, Future}
import scala.util.matching.Regex

@Singleton()
class EligibilityController @Inject() (
  cc:                 ControllerComponents,
  eligibilityService: EligibilityService
)(using ExecutionContext)
    extends BackendController(cc) {

  private val logger: Logger = Logger(this.getClass)

  given CryptoFormat = CryptoFormat.NoOpCryptoFormat

  def insertEligibilityData(): Action[JsObject] = Action.async(parse.json[JsObject]) { implicit request =>
    eligibilityService
      .insertEligibilityData(request.body.as[EligibilityCheckResult])
      .map(_ => Created(s"Inserted eligibility record into MongoDb: [ ${request.body.toString} ]"))
  }

  private val ninoStatusRegex: Regex = "ST(\\d\\d\\d)000A".r

  def retrieveEligibilityData: Action[EligibilityRequest] = Action.async(parse.json[EligibilityRequest]) {
    implicit request =>
      LoggingHelper.logRequestInfo(logger = logger, request = request)

      val ids: List[String]                                    = request.body.identification.map(_.idValue.value)
      val idsWithMatchedStatus: List[(String, Option[Status])] = ids.map {
        case id @ "NotFound"              => (id, Some(NotFound))
        case id @ "500Error"              => (id, Some(InternalServerError))
        case id @ "502Error"              => (id, Some(BadGateway))
        case id @ "503Error"              => (id, Some(ServiceUnavailable))
        case id @ "504Error"              => (id, Some(GatewayTimeout))
        case id @ "422Error"              => (id, Some(UnprocessableEntity)) // de-registered user response from ttp
        case id @ "499Error"              => (id, Some(new Status(499)))     // we see some 499's in prod, they're similar to 502
        case id @ ninoStatusRegex(status) => (id, Some(new Status(status.toInt)))
        case id                           => (id, None)
      }

      val maybeResponse: Option[Status] = idsWithMatchedStatus match {
        case head :: Nil => head._2
        case Nil         => sys.error("Impossible that there was no List")
        case head :: _   =>
          logger.warn("We did not expect more than one matched id. Investigate")
          head._2
      }
      maybeResponse.fold {
        eligibilityService.eligibilityData(request.body).flatMap {
          case Some(value) =>
            LoggingHelper.logResponseInfo(uri = request.uri, logger = logger, responseBody = Json.toJson(value))
            Future.successful(Ok(Json.toJson(value)))
          case None        =>
            Future.successful(
              Ok(
                Json.toJson(
                  EligibilityService.defaultEligibleResponse(request.body.regimeType, request.body.identification)
                )
              )
            )
        }
      }(Future.successful)
  }

  val removeAllRecordsFromEligibilityDb: Action[AnyContent] = Action.async { _ =>
    eligibilityService
      .removeAllRecordsFromEligibilityDb()
      .map(_ => Accepted("Removed all records from Eligibility MongoDb"))
  }

}
