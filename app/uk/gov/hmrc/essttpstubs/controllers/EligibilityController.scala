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

import play.api.libs.json.{Format, Json}
import play.api.mvc.{Action, ControllerComponents}
import uk.gov.hmrc.essttpstubs.controllers.EligibilityController.{EligibilityRequest, ErrorData}
import uk.gov.hmrc.essttpstubs.model.TaxRegime
import uk.gov.hmrc.essttpstubs.services.EligibilityService
import uk.gov.hmrc.essttpstubs.services.EligibilityService.EligibilityError
import uk.gov.hmrc.essttpstubs.ttp.model.TtpEligibilityData
import uk.gov.hmrc.play.bootstrap.backend.controller.BackendController

import javax.inject.{Inject, Singleton}
import scala.concurrent.ExecutionContext

@Singleton()
class EligibilityController @Inject()(cc: ControllerComponents, es: EligibilityService)(implicit ec: ExecutionContext) extends BackendController(cc) {

  def eligibilityData: Action[EligibilityRequest] = Action.async(parse.json[EligibilityRequest]) { implicit request =>
       val regime = TaxRegime.withNameLowercaseOnly(request.body.regimeType.toLowerCase())
       for{
         data <- es.eligibilityData(regime, regime.taxIdOf(request.body.idNumber), request.body.returnFinancials)
       } yield Ok(Json.toJson(data))

  }

  def insertEligibilityData(taxRegime: TaxRegime, id: String): Action[TtpEligibilityData] = Action.async(parse.json[TtpEligibilityData]) { implicit request =>
    for{
      _ <- es.insertEligibilityData(taxRegime, taxRegime.taxIdOf(id), request.body)
    } yield Ok("inserted")

  }

  def errors(taxRegime: TaxRegime, id: String): Action[ErrorData] = Action.async(parse.json[ErrorData]) { implicit request =>
    for{
      _ <- es.insertErrorData(taxRegime, taxRegime.taxIdOf(id), request.body.errors)
    } yield Ok("inserted")

  }





  }

object EligibilityController{

  case class EligibilityRequest(idType: String, idNumber: String, regimeType: String, returnFinancials: Boolean)

  object EligibilityRequest{
     implicit val fmt: Format[EligibilityRequest] = Json.format[EligibilityRequest]
  }

  case class ErrorData(errors: List[EligibilityError])

  object ErrorData{
    implicit val fmt: Format[ErrorData] = Json.format[ErrorData]
  }

}




