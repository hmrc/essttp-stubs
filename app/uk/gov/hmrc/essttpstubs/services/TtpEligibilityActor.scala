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

import akka.actor.typed.scaladsl.Behaviors
import akka.actor.typed.{ActorRef, Behavior}
import uk.gov.hmrc.essttpstubs.model.TaxId
import uk.gov.hmrc.essttpstubs.services.EligibilityService.{EligibilityError, FinancialData, ServiceError}

object TtpEligibilityActor {

  sealed trait Command

  case class MapPayeError(taxId: TaxId, error: EligibilityError, replyTo: ActorRef[Either[ServiceError, Unit]]) extends Command

  case class MapFinancialData(taxId: TaxId, data: FinancialData, replyTo: ActorRef[Either[ServiceError, Unit]]) extends Command

  case class FindEligibilityData(taxId: TaxId, replyTo: ActorRef[Either[ServiceError, FinancialData]]) extends Command

  def apply(): Behavior[Command] = Behaviors.setup[Command] { ctx =>
    ctx.log.info("starting the ttp eligibility actor")
    handler(TTP.Empty)
  }

  def handler(ttp: TTP): Behavior[Command] = Behaviors.receiveMessage[Command] {
    case MapPayeError(taxId, error, replyTo) => replyTo ! Right(())
      handler(ttp.mapError(taxId, error))

    case MapFinancialData(taxId, data, replyTo) => replyTo ! Right(())
      handler(ttp.mapFinancialData(taxId, data))

    case FindEligibilityData(taxId, replyTo) => replyTo ! ttp.eligibilityData(taxId)
      Behaviors.same
  }

}
