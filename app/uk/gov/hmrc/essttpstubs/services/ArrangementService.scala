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

package uk.gov.hmrc.essttpstubs.services

import essttp.rootmodel.ttp.RegimeType
import essttp.rootmodel.ttp.arrangement.{ArrangementRequest, ArrangementResponse, CustomerReference}
import essttp.rootmodel.ttp.eligibility.ProcessingDateTime
import play.api.http.Status.BAD_REQUEST
import uk.gov.hmrc.http.UpstreamErrorResponse

import java.time.format.DateTimeFormatter
import java.time.{Clock, Instant}
import javax.inject.{Inject, Singleton}

@Singleton
class ArrangementService @Inject() (clock: Clock) {

  def enactArrangement(request: ArrangementRequest): ArrangementResponse = {
    val identificationKey = request.regimeType match {
      case RegimeType.EPAYE => "BROCS"
      case RegimeType.VAT   => "VRN"
      case RegimeType.SA    => "UTR"
      case RegimeType.SIMP  => "NINO"
    }
    request.identification.find(_.idType.value == identificationKey) match {
      case None     =>
        throw UpstreamErrorResponse(s"Could not find ID with ID type '$identificationKey'", BAD_REQUEST)
      case Some(id) =>
        val now = DateTimeFormatter.ISO_INSTANT.format(Instant.now(clock))
        ArrangementResponse(ProcessingDateTime(now), CustomerReference(id.idValue.value))
    }
  }

}
