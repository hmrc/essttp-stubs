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

import play.api.libs.json.Json
import play.api.mvc.ControllerComponents
import uk.gov.hmrc.essttpstubs.model.{IdType, OverduePayments, TaxRegime}
import uk.gov.hmrc.essttpstubs.services.EligibilityService
import uk.gov.hmrc.play.bootstrap.backend.controller.BackendController

import javax.inject.{Inject, Singleton}
import scala.concurrent.ExecutionContext

@Singleton()
class EligibilityController @Inject()(cc: ControllerComponents, es: EligibilityService)(implicit ec: ExecutionContext) extends BackendController(cc) {

  def error(regime: TaxRegime, idType: IdType, id: String) = Action.async(parse.json) { implicit request =>
    withJsonBody[EligibilityService.EligibilityError]{ error =>
      val result = es.error(regime, idType, regime.taxIdOf(idType, id), error)
      ???
    }
  }

  def financials(regime: TaxRegime, idType: IdType, id: String) = Action.async(parse.json) { implicit request =>
    withJsonBody[OverduePayments]{ financials =>
      val result = es.financials(regime, idType, regime.taxIdOf(idType, id), financials).map(_ => Ok("setup financail data"))
      result.getOrElse(throw new IllegalArgumentException("should not happen"))
    }
  }

  def eligibilityData(regime: TaxRegime, idType: IdType, id: String) = Action.async { implicit request =>
    val result = for{
      data <- es.eligibilityData(regime, idType, regime.taxIdOf(idType, id))
    } yield Ok(Json.toJson(data))

    result.getOrElse(throw new Exception("failed to retrieve eligibility data"))


  }
}




