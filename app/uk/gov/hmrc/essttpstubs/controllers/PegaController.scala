/*
 * Copyright 2024 HM Revenue & Customs
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

import play.api.Logger
import play.api.libs.json.Json
import play.api.mvc.{Action, AnyContent, ControllerComponents}
import uk.gov.hmrc.play.bootstrap.backend.controller.BackendController

import javax.inject.{Inject, Singleton}
import scala.util.Random

@Singleton
class PegaController @Inject() (cc: ControllerComponents) extends BackendController(cc) {

  val logger: Logger = Logger(this.getClass)

  val token: Action[AnyContent] = Action { implicit request =>
    val response = Json.parse(
      s"""{
        |  "access_token": "${nextAlphanumericString(20)}",
        |  "token_type": "bearer",
        |  "expires_in": 360000
        |}
        |""".stripMargin
    )
    logger.info(
      s"Got request for PEGA token with headers ${request.headers.headers.toString} and " +
        s"body ${request.body.toString}. Responding with ${response.toString}"
    )
    Ok(response)
  }

  val startCase: Action[AnyContent] = Action { implicit request =>
    val response = Json.parse(
      s"""{
         |  "ID": "${nextAlphanumericString(20)}",
         |  "data": {
         |    "caseInfo": {
         |      "assignments": [
         |        { "ID": "${nextAlphanumericString(20)}" },
         |        { "ID": "${nextAlphanumericString(20)}" }
         |      ]
         |    }
         |  }
         |}
         |""".stripMargin
    )

    logger.info(
      s"Got request for PEGA start case headers ${request.headers.headers.toString} and " +
        s"body ${request.body.toString}. Responding with ${response.toString}"
    )
    Created(response)
  }

  def getCase(caseId: String): Action[AnyContent] = Action { implicit request =>
    val response = Json.parse(
      """{
         |  "paymentPlan": {
         |    "numberOfInstalments": 4,
         |    "planDuration": 4,
         |    "totalDebt": 997700,
         |    "totalDebtIncInt": 997816,
         |    "planInterest": 116,
         |    "collections": {
         |      "initialCollection": {
         |        "dueDate": "2024-08-27",
         |        "amountDue": 2300
         |      },
         |      "regularCollections": [
         |        {
         |          "dueDate": "2024-10-28",
         |          "amountDue": 249454
         |        },
         |        {
         |          "dueDate": "2024-11-28",
         |          "amountDue": 249454
         |        },
         |        {
         |          "dueDate": "2024-12-28",
         |          "amountDue": 249454
         |        },
         |        {
         |          "dueDate": "2025-01-28",
         |          "amountDue": 249454
         |        }
         |      ]
         |    },
         |    "instalments": [
         |      {
         |        "instalmentNumber": 4,
         |        "dueDate": "2025-01-28",
         |        "instalmentInterestAccrued": 29,
         |        "instalmentBalance": 249425,
         |        "debtItemChargeId": "A00000000001",
         |        "amountDue": 249425,
         |        "debtItemOriginalDueDate": "2023-09-28"
         |      },
         |      {
         |        "instalmentNumber": 3,
         |        "dueDate": "2024-12-28",
         |        "instalmentInterestAccrued": 29,
         |        "instalmentBalance": 498850,
         |        "debtItemChargeId": "A00000000001",
         |        "amountDue": 249425,
         |        "debtItemOriginalDueDate": "2023-09-28"
         |      },
         |      {
         |        "instalmentNumber": 2,
         |        "dueDate": "2024-11-28",
         |        "instalmentInterestAccrued": 29,
         |        "instalmentBalance": 748275,
         |        "debtItemChargeId": "A00000000001",
         |        "amountDue": 249425,
         |        "debtItemOriginalDueDate": "2023-09-28"
         |      },
         |      {
         |        "instalmentNumber": 1,
         |        "dueDate": "2024-10-28",
         |        "instalmentInterestAccrued": 29,
         |        "instalmentBalance": 997700,
         |        "debtItemChargeId": "A00000000001",
         |        "amountDue": 249425,
         |        "debtItemOriginalDueDate": "2023-09-28"
         |      }
         |    ]
         |  }
         |}
         |""".stripMargin
    )

    logger.info(
      s"Got request for PEGA get case for case ID ${caseId} with headers ${request.headers.headers.toString} and " +
        s"body ${request.body.toString}. Responding with ${response.toString}"
    )
    Ok(response)
  }

  private def nextAlphanumericString(length: Int) = Random.alphanumeric.take(length).mkString("")

}
