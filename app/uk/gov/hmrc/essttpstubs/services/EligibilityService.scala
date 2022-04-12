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

import akka.actor.typed.scaladsl.AskPattern._
import akka.actor.typed.{ActorRef, Scheduler}
import akka.util.Timeout
import cats.data.EitherT
import enumeratum.{Enum, EnumEntry}
import play.api.libs.json.{Format}
import play.api.libs.functional.syntax._
import uk.gov.hmrc.essttpstubs.model.{TaxId, TaxRegime}
import uk.gov.hmrc.essttpstubs.services.EligibilityService.EligibilityError
import uk.gov.hmrc.essttpstubs.services.TtpEligibilityActor.{Command, FindEligibilityData, InsertEligibilityData, InsertErrorData}
import uk.gov.hmrc.essttpstubs.ttp.model.TtpEligibilityData

import javax.inject.{Inject, Singleton}
import scala.concurrent.Future
import scala.concurrent.duration._

@Singleton()
class EligibilityService @Inject()(ttp: ActorRef[Command])(implicit S: Scheduler) {

  implicit val timeout = Timeout(5.seconds)

  def eligibilityData(regime: TaxRegime, id: TaxId, returnFinancials: Boolean): Future[TtpEligibilityData] = {
    ttp.ask(ref => FindEligibilityData(id, returnFinancials,ref))
  }

  def insertEligibilityData(regime: TaxRegime, id: TaxId, data: TtpEligibilityData): Future[Unit] = {
    ttp.ask(ref => InsertEligibilityData(id, data,ref))
  }

  def insertErrorData(regime: TaxRegime, id: TaxId, data: List[EligibilityError]): Future[Unit] = {
    ttp.ask(ref => InsertErrorData(id, data,ref))
  }

}

object EligibilityService {

  type SR[A] = EitherT[Future,ServiceError, A]


  sealed trait ServiceError

  case class InternalServiceError(msg: String) extends ServiceError

  sealed trait EligibilityError extends EnumEntry with ServiceError

  object EligibilityError extends Enum[EligibilityError]{
    object DebtIsTooLarge extends EligibilityError{
      override val entryName = "Debt is too large"
    }
    object DebtIsTooOld extends EligibilityError{
      override val entryName = "Debt is too old"
    }
    object ReturnsAreNotUpToDate extends EligibilityError{
      override val entryName = "Returns are not up to date"
    }
    object YouAlreadyHaveAPaymentPlan extends EligibilityError{
      override val entryName = "you already have a payment plan"
    }
    object OutstandingPenalty extends EligibilityError{
      override val entryName = "Outstanding penalty"
    }
    object PayeIsInsolvent extends EligibilityError{
      override val entryName = "Paye is insolvent"
    }
    object PayeHasDisallowedCharges extends EligibilityError{
      override val entryName = "Paye has disallowed charges"
    }
    object RLSFlagIsSet extends EligibilityError{
      override val entryName = "RLS flag is set"
    }

    override val values = findValues

    implicit val format: Format[EligibilityError] = implicitly[Format[String]].inmap(EligibilityError.withName, _.entryName)

  }
}
