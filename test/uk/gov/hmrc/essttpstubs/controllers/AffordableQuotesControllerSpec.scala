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

package uk.gov.hmrc.essttpstubs.controllers

import com.typesafe.config.ConfigFactory
import essttp.rootmodel.ttp.affordablequotes.{AffordableQuotesResponse, PaymentPlanAffordableAmount, TotalDebt}
import essttp.rootmodel.{AmountInPence, UpfrontPaymentAmount}
import play.api.Configuration
import play.api.libs.json.{JsObject, JsValue, Json}
import uk.gov.hmrc.essttpstubs.testutil.connector.TestAffordableQuotesConnector
import uk.gov.hmrc.essttpstubs.testutil.{ItSpec, TdAll}
import uk.gov.hmrc.http.HttpResponse

class AffordableQuotesControllerSpec extends ItSpec {

  override lazy val additionalConfig: Configuration = Configuration(
    ConfigFactory.parseString(
      """
        |affordability {
        |  instalment-amounts {
        |    base-interest-rate = 1.0
        |    additional-interest-rate = 2.5
        |  }
        |}
        |""".stripMargin
    )
  )

  lazy val testAffordableQuotesConnector: TestAffordableQuotesConnector = injector.instanceOf[TestAffordableQuotesConnector]
  val totalDebt6k: TotalDebt = TotalDebt(AmountInPence(600000))

  "AffordableQuotesController.affordableQuotes should" - {
    "return payment plans with 1, 2 and 3 month options when optimum is 1 or 2" in {
      val affordableAmount: PaymentPlanAffordableAmount = PaymentPlanAffordableAmount(AmountInPence(350000))
      val expectedResult = TdAll.AffordabilityJsonBodies.`1-2-3`
      val request: JsValue = affordableQuotesRequest(totalDebt6k, affordableAmount)
      val result: HttpResponse = testAffordableQuotesConnector.calculateAffordableQuotes(request).futureValue
      result.json shouldBe Json.toJson(expectedResult)
      result.json.as[AffordableQuotesResponse].paymentPlans.map(_.collections.regularCollections.size) shouldBe List(1, 2, 3)
    }

    "return payment plans with 2, 3 and 4 month options when optimum is 3" in {
      val affordableAmount: PaymentPlanAffordableAmount = PaymentPlanAffordableAmount(AmountInPence(250000))
      val expectedResult = TdAll.AffordabilityJsonBodies.`2-3-4`
      val request: JsValue = affordableQuotesRequest(totalDebt6k, affordableAmount)
      val result: HttpResponse = testAffordableQuotesConnector.calculateAffordableQuotes(request).futureValue
      result.json shouldBe Json.toJson(expectedResult)
      result.json.as[AffordableQuotesResponse].paymentPlans.map(_.collections.regularCollections.size) shouldBe List(2, 3, 4)
    }
    "return payment plans with 3, 4 and 5 month options when optimum is 4" in {
      val affordableAmount: PaymentPlanAffordableAmount = PaymentPlanAffordableAmount(AmountInPence(200000))
      val expectedResult = TdAll.AffordabilityJsonBodies.`3-4-5`
      val request: JsValue = affordableQuotesRequest(totalDebt6k, affordableAmount)
      val result: HttpResponse = testAffordableQuotesConnector.calculateAffordableQuotes(request).futureValue
      result.json shouldBe Json.toJson(expectedResult)
      result.json.as[AffordableQuotesResponse].paymentPlans.map(_.collections.regularCollections.size) shouldBe List(3, 4, 5)
    }
    "return payment plans with 4, 5 and 6 month options when optimum is 5 or 6" in {
      val affordableAmount: PaymentPlanAffordableAmount = PaymentPlanAffordableAmount(AmountInPence(130000))
      val expectedResult = TdAll.AffordabilityJsonBodies.`4-5-6`
      val request: JsValue = affordableQuotesRequest(totalDebt6k, affordableAmount)
      val result: HttpResponse = testAffordableQuotesConnector.calculateAffordableQuotes(request).futureValue
      result.json shouldBe Json.toJson(expectedResult)
      result.json.as[AffordableQuotesResponse].paymentPlans.map(_.collections.regularCollections.size) shouldBe List(4, 5, 6)
    }
    "return payment plans containing initialPayment in collections when there is an upfront payment in request" in {
      val affordableAmount: PaymentPlanAffordableAmount = PaymentPlanAffordableAmount(AmountInPence(130000))
      val expectedResult = TdAll.AffordabilityJsonBodies.`4-5-6-withUpfrontPayment`
      val request: JsValue = affordableQuotesRequest(totalDebt6k, affordableAmount, Some(UpfrontPaymentAmount(AmountInPence(1000))))
      val result: HttpResponse = testAffordableQuotesConnector.calculateAffordableQuotes(request).futureValue
      result.json shouldBe Json.toJson(expectedResult)
      val resultAsAffordableQuotesResponse = result.json.as[AffordableQuotesResponse]
      resultAsAffordableQuotesResponse.paymentPlans.map(_.collections.regularCollections.size) shouldBe List(4, 5, 6)
    }
    "return plans 1,2,3 when affordable amount is more than total debt (edge case)" in {
      val affordableAmount: PaymentPlanAffordableAmount = PaymentPlanAffordableAmount(AmountInPence(350000000))
      val expectedResult = TdAll.AffordabilityJsonBodies.`1-2-3`
      val request: JsValue = affordableQuotesRequest(totalDebt6k, affordableAmount)
      val result: HttpResponse = testAffordableQuotesConnector.calculateAffordableQuotes(request).futureValue
      result.json shouldBe Json.toJson(expectedResult)
      result.json.as[AffordableQuotesResponse].paymentPlans.map(_.collections.regularCollections.size) shouldBe List(1, 2, 3)
    }
    "not return plan where the collection amount is less than £1" in {
      val affordableAmount: PaymentPlanAffordableAmount = PaymentPlanAffordableAmount(AmountInPence(200))
      val request: JsValue = affordableQuotesRequest(TotalDebt(AmountInPence(240)), affordableAmount)
      val response: AffordableQuotesResponse = testAffordableQuotesConnector.calculateAffordableQuotes(request).futureValue.json.as[AffordableQuotesResponse]
      // plan with 3 instalments shouldn't appear since dividing £2.40 into 3 instalments would take the collection amount less than £1
      response.paymentPlans.map(_.collections.regularCollections.size) shouldBe List(1, 2)
    }
  }

  def affordableQuotesRequest(totalDebt: TotalDebt, affordableAmount: PaymentPlanAffordableAmount, upfrontPaymentAmount: Option[UpfrontPaymentAmount] = None): JsObject = {
    val upfrontPaymentJson: String = upfrontPaymentAmount.fold("")(someValue =>
      s"""
         |"initialPaymentDate":"2022-06-18",
         |"initialPaymentAmount":${someValue.value.value},
         |""".stripMargin)
    Json.parse(
      s"""
         |{
         |   "channelIdentifier":"eSSTTP",
         |   "paymentPlanAffordableAmount":${affordableAmount.value.value},
         |   "paymentPlanFrequency":"Monthly",
         |   "paymentPlanMaxLength":6,
         |   "paymentPlanMinLength":1,
         |   "accruedDebtInterest":1000,
         |   "paymentPlanStartDate":"2022-07-08",
         |   $upfrontPaymentJson
         |   "debtItemCharges": [
         |     {
         |       "outstandingDebtAmount": ${totalDebt.value.value},
         |       "mainTrans": "2000",
         |       "subTrans": "1000",
         |       "debtItemChargeId": "XW006559808862",
         |       "interestStartDate": "2022-05-22",
         |       "debtItemOriginalDueDate": "2022-05-22"
         |     }
         |   ],
         |   "customerPostcodes": [
         |     {
         |        "addressPostcode":"BN127ER",
         |        "postcodeDate":"2022-05-22"
         |     },
         |     {
         |        "addressPostcode":"BN129ER",
         |        "postcodeDate":"2022-04-30"
         |     }
         |   ]
         |}
         |""".stripMargin
    ).as[JsObject]
  }
}
