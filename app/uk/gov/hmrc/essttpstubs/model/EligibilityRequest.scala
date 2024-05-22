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

import essttp.rootmodel.ttp.eligibility.Identification
import play.api.libs.json.{Format, Json}

final case class EligibilityRequest(
    channelIdentifier:         String,
    identification:            List[Identification],
    regimeType:                String,
    returnFinancialAssessment: Boolean
)

object EligibilityRequest {
  @SuppressWarnings(Array("org.wartremover.warts.Any"))
  implicit val format: Format[EligibilityRequest] = Json.format[EligibilityRequest]
}
