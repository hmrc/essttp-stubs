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

import uk.gov.hmrc.essttpstubs.model.TaxId
import uk.gov.hmrc.essttpstubs.services.EligibilityService.{EligibilityError, FinancialData}
import uk.gov.hmrc.essttpstubs.services.TTP.asError

case class TTP(items: Map[TaxId, Either[EligibilityError, FinancialData]]){

  def mapError(taxID: TaxId, error: EligibilityError): TTP = TTP(items + (taxID -> asError(error)))

  def mapFinancialData(taxID: TaxId, data: FinancialData): TTP = TTP(items + (taxID -> Right(data)))

  def eligibilityData(taxID: TaxId): Either[EligibilityError,FinancialData] = items(taxID)

}

object TTP {

  val Empty: TTP = TTP(Map.empty)

  def asError(error: EligibilityError): Either[EligibilityError, FinancialData] = Left(error)

}
