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

package uk.gov.hmrc.essttpstubs.services

import akka.actor.typed.{ActorRef, Scheduler}
import cats.data.EitherT
import uk.gov.hmrc.essttpstubs.model.{IdType, TaxId, TaxRegime}
import uk.gov.hmrc.essttpstubs.services.EligibilityService.{EligibilityError, FinancialData, SR, ServiceError}
import akka.actor.typed.scaladsl.AskPattern._
import akka.util.Timeout
import play.api.libs.json.{Format, Json}
import uk.gov.hmrc.essttpstubs.services.TtpEligibilityActor.{FindEligibilityData, MapFinancialData, MapPayeError}

import javax.inject.{Inject, Singleton}
import scala.concurrent.duration._
import scala.concurrent.Future

@Singleton()
class EligibilityService @Inject()(ttp: ActorRef[TtpEligibilityActor.Command])(implicit S: Scheduler) {

  implicit val timeout = Timeout(5.seconds)

  def error(regime: TaxRegime, idType: IdType, id: TaxId, error: EligibilityError): SR[Unit] = {
    EitherT(ttp.ask(ref => MapPayeError(id, error,ref)))
  }

  def financials(regime: TaxRegime, idType: IdType, id: TaxId, data: FinancialData): SR[Unit] = {
    EitherT(ttp.ask(ref => MapFinancialData(id, data,ref)))
  }

  def eligibilityData(regime: TaxRegime, idType: IdType, id: TaxId): SR[FinancialData] = {
    EitherT(ttp.ask(ref => FindEligibilityData(id,ref)))
  }

}

object EligibilityService {

  type SR[A] = EitherT[Future,ServiceError, A]

  case class FinancialData(name: String)

  object FinancialData{
    implicit val fmt: Format[FinancialData] = Json.format[FinancialData]
  }

  sealed trait ServiceError

  case class InternalServiceError(msg: String) extends ServiceError

  case class EligibilityError private(ordinal: Int, name: String) extends ServiceError

  object EligibilityError {
    val DebitIsTooLarge = EligibilityError(0, "Debit is too large")
    val DebitIsTooOld = EligibilityError(1, "Debit is too old")
    val ReturnsNotUpToDate = EligibilityError(2, "Returns are not up to date")
    val YouAlreadyHaveAPaymentPlan = EligibilityError(3, "Debit is too large")
    val OutstandingPenalty = EligibilityError(4, "Outstanding Penalty")
    val PayeIsInsolvent = EligibilityError(5, "Paye is Insolvent")
    val PayeHasDisallowedCharges = EligibilityError(6, "Paye has disallowed charges")
    val RLSFlagIsSet = EligibilityError(7, "RLS flag is set")

    implicit val fmt: Format[EligibilityError] = Json.format[EligibilityError]
  }
}
