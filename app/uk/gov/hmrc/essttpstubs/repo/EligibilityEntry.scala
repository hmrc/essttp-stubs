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

package uk.gov.hmrc.essttpstubs.repo

import essttp.crypto.CryptoFormat
import essttp.rootmodel.ttp.eligibility.EligibilityCheckResult
import play.api.libs.json.{Format, Json, OFormat}
import uk.gov.hmrc.mongo.play.json.formats.MongoJavatimeFormats

import java.time.Instant

final case class EligibilityEntry(eligibilityCheckResult: EligibilityCheckResult, createdAt: Instant)

object EligibilityEntry {

  @SuppressWarnings(Array("org.wartremover.warts.Any"))
  given format: OFormat[EligibilityEntry] = {
    given Format[Instant] = MongoJavatimeFormats.instantFormat
    given CryptoFormat    = CryptoFormat.NoOpCryptoFormat

    Json.format[EligibilityEntry]
  }

}
