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

import org.scalatest.matchers.should.Matchers
import org.scalatest.wordspec.AnyWordSpec
import play.api.libs.json.Json
import uk.gov.hmrc.essttpstubs.ttp.model._

class TTPSpec extends AnyWordSpec with Matchers {

  "TTP" when {
    "the service is available" should {
      "return financial data" in {
        val taxPeriodCharges = TaxPeriodCharges(
          "T5545454554",
          "22000",
          "",
          "1000",
          "",
          100000,
          "2017-03-07",
          15.97,
          true,
          ChargeLocks(
            PaymentLock(false, ""),
            PaymentLock(false, ""),
            PaymentLock(false, ""),
            PaymentLock(false, ""),
            PaymentLock(false, "")
          )
        )

        val chargeTypeAssessments: List[ChargeTypeAssessment] = List(
          ChargeTypeAssessment("2020-08-13","2020-08-14",300000,List(taxPeriodCharges)))

        val data = TtpEligibilityData(
          "SSTTP",
          "A00000000001",
          "PAYE",
          "2022-03-10",
          CustomerDetails("NI", "B5 7LN"),
          EligibilityStatus(false, 1, 6),
          EligibilityRules(true,"Receiver is not known",false,false,false,false,false,300,600,false,false,false),
          FinancialLimitBreached(true, 16000),
          chargeTypeAssessments
        )
        val js = Json.toJson(data)
        val result = Json.fromJson[TtpEligibilityData](js)
        result.get shouldBe data
      }
    }
  }

}

