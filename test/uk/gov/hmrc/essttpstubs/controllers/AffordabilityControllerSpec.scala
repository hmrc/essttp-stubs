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
import essttp.rootmodel.AmountInPence
import essttp.rootmodel.ttp.affordability.InstalmentAmounts
import play.api.Configuration
import play.api.http.Status.BAD_REQUEST
import play.api.libs.json.{JsArray, JsObject, JsValue, Json}
import uk.gov.hmrc.essttpstubs.controllers.AffordabilityControllerSpec.InstalmentAmountsTestCase
import uk.gov.hmrc.essttpstubs.testutil.ItSpec
import uk.gov.hmrc.essttpstubs.testutil.connector.TestAffordabilityConnector

import scala.util.Random

class AffordabilityControllerSpec extends ItSpec {

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

  lazy val testAffordabilityConnector: TestAffordabilityConnector = injector.instanceOf[TestAffordabilityConnector]

  "AffordabilityController" - {

    ".calculateInstalmentAmounts should return BadRequest when total debt is equal to initial payment amount" in {
      val request = instalmentAmountsRequest(InstalmentAmountsTestCase(1, 6, Some(1000), List(500, 500)))
      val response = testAffordabilityConnector.calculateInstalmentAmounts(request).failed.futureValue
      asUpstreamErrorResponse(response).statusCode shouldBe BAD_REQUEST
    }

    ".calculateInstalmentAmounts should return BadRequest when total debt is strictly less than the initial payment amount" in {
      val request = instalmentAmountsRequest(InstalmentAmountsTestCase(1, 6, Some(1000), List(900)))
      val response = testAffordabilityConnector.calculateInstalmentAmounts(request).failed.futureValue
      asUpstreamErrorResponse(response).statusCode shouldBe BAD_REQUEST
    }

    ".calculateInstalmentAmounts should return an Ok with the correct instalment amounts when passed a valid request" in {
        def instalmentAmounts(min: Long, max: Long): InstalmentAmounts =
          InstalmentAmounts(AmountInPence(min), AmountInPence(max))

      val testCases: List[(String, InstalmentAmountsTestCase, InstalmentAmounts)] =
        List(
          ("1", InstalmentAmountsTestCase(1, 6, None, List(300000 - 1, 1)), instalmentAmounts(55250, 300875)),
          ("2", InstalmentAmountsTestCase(1, 6, Some(150000), List(900000)), instalmentAmounts(138125, 752188)),
          ("3", InstalmentAmountsTestCase(1, 6, None, List(2000)), instalmentAmounts(368, 2006)),
          ("4", InstalmentAmountsTestCase(1, 6, Some(1900), List(1800, 200)), instalmentAmounts(18, 100))
        )

      testCases.foreach {
        case (id, testCase, expectedResult) =>
          withClue(s"For test case $id: ") {
            val request = instalmentAmountsRequest(testCase)
            val response = testAffordabilityConnector.calculateInstalmentAmounts(request).futureValue
            response.json shouldBe Json.toJson(expectedResult)
          }
      }
    }

  }

  def instalmentAmountsRequest(testCase: InstalmentAmountsTestCase): JsValue = {
    val initialPaymentDetails = testCase.initialPaymentAmount.map((amount: Int) =>
      Json.parse(
        s"""
           |{
           |  "initialPaymentDate": "2022-03-02",
           |  "initialPaymentAmount": $amount
           |}
           |""".stripMargin
      ).as[JsObject])

    val debtItemCharges = {
      val array = JsArray(
        testCase.outstandingDebtAmounts.map { amount =>
          Json.parse(
            s"""
               |{
               |  "outstandingDebtAmount": $amount,
               |  "mainTrans":"1525",
               |  "subTrans":"1000",
               |  "debtItemChargeId":"ChargeRef ${Random.nextInt(1000)}",
               |  "interestStartDate":"2021-09-03",
               |  "debtItemOriginalDueDate":"2021-09-03"
               |}
               |""".stripMargin
          ).as[JsObject]
        }
      )
      JsObject(Map("debtItemCharges" -> array))
    }

    val json = Json.parse(
      s"""
         |{
         |    "channelIdentifier": "eSSTTP",
         |    "paymentPlanFrequency": "Monthly",
         |    "paymentPlanMinLength": ${testCase.minPlanLength},
         |    "paymentPlanMaxLength": ${testCase.maxPlanLength},
         |    "interestAccrued": 500,
         |    "earliestPaymentPlanStartDate": "2022-03-03",
         |    "latestPaymentPlanStartDate": "2022-03-28",
         |    "accruedDebtInterest": 1326,
         |    "customerPostcodes": [
         |       {
         |        "addressPostcode": "BN127ER",
         |        "postcodeDate": "2022-05-22"
         |      },
         |      {
         |        "addressPostcode": "BN129ER",
         |        "postcodeDate": "2022-04-30"
         |      }
         |    ]
         |}
         |""".stripMargin
    ).as[JsObject]

    json ++ initialPaymentDetails.getOrElse(JsObject.empty) ++ debtItemCharges
  }

}

object AffordabilityControllerSpec {

  final case class InstalmentAmountsTestCase(
      minPlanLength: Int, maxPlanLength: Int, initialPaymentAmount: Option[Int], outstandingDebtAmounts: List[Int]
  )

}
