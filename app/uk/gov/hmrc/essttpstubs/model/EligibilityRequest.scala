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

package uk.gov.hmrc.essttpstubs.model

import essttp.rootmodel.ttp.RegimeType
import essttp.rootmodel.ttp.eligibility.{IdType, IdValue, Identification}
import play.api.libs.functional.syntax._
import play.api.libs.json._

final case class EligibilityRequest(
  channelIdentifier:         String,
  identification:            List[Identification],
  regimeType:                RegimeType,
  returnFinancialAssessment: Boolean
)

object EligibilityRequest {

  given Format[EligibilityRequest] = {
    val reads: Reads[EligibilityRequest] =
      Json.reads[EligibilityRequest] orElse
        (
          (__ \ "channelIdentifier").read[String] and
            (__ \ "idType").read[String] and
            (__ \ "idValue").read[String] and
            (__ \ "regimeType").read[RegimeType] and
            (__ \ "returnFinancialAssessment").read[Boolean]
        )((channelIdentifier, idType, idValue, regimeType, returnFinancialAssessment) =>
          EligibilityRequest(
            channelIdentifier,
            List(Identification(IdType(idType), IdValue(idValue))),
            regimeType,
            returnFinancialAssessment
          )
        )

    val writes: OWrites[EligibilityRequest] = Json.writes[EligibilityRequest]

    Format(reads, writes)
  }
}
