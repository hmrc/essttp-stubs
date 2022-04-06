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

import uk.gov.hmrc.essttpstubs.model._
import uk.gov.hmrc.essttpstubs.services.EligibilityService.EligibilityError
import uk.gov.hmrc.essttpstubs.services.TTP.asError

import java.time.LocalDate

case class TTP(items: Map[TaxID, Either[EligibilityError, OverduePayments]]){

  def mapError(taxID: TaxID, error: EligibilityError): TTP = TTP(items + (taxID -> asError(error)))

  def mapFinancialData(taxID: TaxID, data: OverduePayments): TTP = TTP(items + (taxID -> Right(data)))

  def eligibilityData(taxID: TaxID): Either[EligibilityError,OverduePayments] = items.getOrElse(taxID, Right(TTP.Default))

}

object TTP {

  val Empty: TTP = TTP(Map.empty)

  def asError(error: EligibilityError): Either[EligibilityError, OverduePayments] = Left(error)

  val qualifyingDebt: AmountInPence = AmountInPence(296345)

  val Default = OverduePayments(
    total = qualifyingDebt,
    payments = List(
      OverduePayment(
        InvoicePeriod(
          monthNumber = 7,
          dueDate = LocalDate.of(2022, 1, 22),
          start = LocalDate.of(2021, 11, 6),
          end = LocalDate.of(2021, 12, 5)),
        amount = AmountInPence((qualifyingDebt.value * 0.4).longValue())),
      OverduePayment(
        InvoicePeriod(
          monthNumber = 8,
          dueDate = LocalDate.of(2021, 12, 22),
          start = LocalDate.of(2021, 10, 6),
          end = LocalDate.of(2021, 11, 5)),
        amount = AmountInPence((qualifyingDebt.value * 0.6).longValue()))))

}
