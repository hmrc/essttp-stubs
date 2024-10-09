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

import cats.implicits.catsSyntaxEq
import play.api.Logger
import play.api.libs.json.{JsValue, Json}
import play.api.mvc._
import uk.gov.hmrc.essttpstubs.config.AppConfig
import uk.gov.hmrc.essttpstubs.model.PegaOauthToken
import uk.gov.hmrc.essttpstubs.repo.PegaTokenRepo
import uk.gov.hmrc.play.bootstrap.backend.controller.BackendController

import java.time.{LocalDateTime, ZoneOffset}
import javax.inject.{Inject, Singleton}
import scala.concurrent.duration.FiniteDuration
import scala.concurrent.{ExecutionContext, Future}
import scala.util.Random

@Singleton
class PegaController @Inject() (cc: ControllerComponents, repo: PegaTokenRepo, appConfig: AppConfig)(implicit ec: ExecutionContext) extends BackendController(cc) {

  val logger: Logger = Logger(this.getClass)

  private val pegaTokenExpiryTime: FiniteDuration = appConfig.pegaTokenExpiryTime

  val token: Action[AnyContent] = Action.async { implicit request =>

    repo.findPegaToken().flatMap {
      case None =>
        generateAndSaveToken()

      case Some(pegaToken) =>
        val expirationTime = pegaToken.validFrom.plusSeconds(pegaTokenExpiryTime.toSeconds)
        if (LocalDateTime.now().isAfter(expirationTime)) {
          generateAndSaveToken()
        } else {
          val remainingTime = expirationTime.toEpochSecond(ZoneOffset.UTC) - LocalDateTime.now().toEpochSecond(ZoneOffset.UTC)
          val response = generateTokenResponse(pegaToken.accessToken, remainingTime.toInt * 2) // we are multiplying by 2 so we can simulate the backend receiving a 401 for expired token
          logRequest(request, response)
          Future.successful(Ok(response))
        }
    }
  }

  private def generateAndSaveToken()(implicit request: Request[AnyContent]): Future[Result] = {
    val newToken = PegaOauthToken(nextAlphanumericString(20), LocalDateTime.now())
    repo.insertPegaToken(newToken).map { _ =>
      val response = generateTokenResponse(newToken.accessToken, pegaTokenExpiryTime.toSeconds * 2) // we are multiplying by 2 so we can simulate the backend receiving a 401 for expired token
      logRequest(request, response)
      Ok(response)
    }
  }

  private def generateTokenResponse(accessToken: String, expiresIn: Long): JsValue = {
    Json.parse(
      s"""{
         |  "access_token": "$accessToken",
         |  "token_type": "bearer",
         |  "expires_in": ${expiresIn.toString}
         |}
         |""".stripMargin
    )
  }

  private def logRequest(request: Request[AnyContent], response: JsValue): Unit = {
    logger.info(
      s"Got request for PEGA token with headers ${request.headers.headers.toString} and " +
        s"body ${request.body.toString}. Responding with ${response.toString}"
    )
  }

  val startCase: Action[AnyContent] = Action.async { implicit request =>
    validateToken().map {
      case Left(errorResult) => errorResult
      case Right(_) =>
        val response = generateStartCaseResponse
        logRequest(request, response)
        Created(Json.toJson(response))
    }
  }

  private val generateStartCaseResponse: JsValue = {
    Json.parse(
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
  }

  private def parseBearerToken(header: String): Option[String] = {
    if (header.startsWith("Bearer ")) Some(header.substring(7))
    else None
  }

  def getCase(caseId: String, viewType: String, pageName: String, getBusinessDataOnly: Boolean): Action[AnyContent] =
    Action.async { implicit request =>
      validateToken().map {
        case Left(errorResult) => errorResult
        case Right(_) =>
          val response = generateGetCaseResponse
          logRequest(request, response)
          Created(Json.toJson(response))
      }
    }

  private def validateToken()(implicit request: Request[AnyContent]): Future[Either[Result, Unit]] = {
    val maybeAuthToken = request.headers.get("Authorization").flatMap(parseBearerToken)
    maybeAuthToken match {
      case Some(authToken) =>
        repo.findPegaToken().flatMap {
          case Some(pegaToken) =>
            val expirationTime = pegaToken.validFrom.plusSeconds(pegaTokenExpiryTime.toSeconds)
            if (LocalDateTime.now().isAfter(expirationTime)) {
              Future.successful(Left(Unauthorized("Token expired")))
            } else if (pegaToken.accessToken =!= authToken) {
              Future.successful(Left(Unauthorized("Token doesn't match")))
            } else {
              Future.successful(Right(()))
            }
          case None =>
            generateAndSaveToken().map(_ => Left(Unauthorized("Token not found in mongo")))
        }
      case None =>
        Future.successful(Left(Unauthorized("Authorization header missing or invalid format")))
    }
  }

  private val generateGetCaseResponse = Json.parse(
    """{
       |  "AA": {
       |   "paymentDay": "28",
       |   "paymentPlan": [
       |     {
       |       "planSelected": true,
       |       "numberOfInstalments": 4,
       |       "planDuration": 4,
       |       "totalDebt": 997700,
       |       "totalDebtIncInt": 997816,
       |       "planInterest": 116,
       |       "collections": {
       |         "initialCollection": {
       |           "dueDate": "2024-08-27",
       |           "amountDue": 2300
       |         },
       |         "regularCollections": [
       |           {
       |             "dueDate": "2024-10-28",
       |             "amountDue": 249454
       |           },
       |           {
       |             "dueDate": "2024-11-28",
       |             "amountDue": 249454
       |           },
       |           {
       |             "dueDate": "2024-12-28",
       |             "amountDue": 249454
       |           },
       |           {
       |             "dueDate": "2025-01-28",
       |             "amountDue": 249454
       |           }
       |         ]
       |       },
       |       "instalments": [
       |         {
       |           "instalmentNumber": 4,
       |           "dueDate": "2025-01-28",
       |           "instalmentInterestAccrued": 29,
       |           "instalmentBalance": 249425,
       |           "debtItemChargeId": "A00000000001",
       |           "amountDue": 249425,
       |           "debtItemOriginalDueDate": "2023-09-28"
       |         },
       |         {
       |           "instalmentNumber": 3,
       |           "dueDate": "2024-12-28",
       |           "instalmentInterestAccrued": 29,
       |           "instalmentBalance": 498850,
       |           "debtItemChargeId": "A00000000001",
       |           "amountDue": 249425,
       |           "debtItemOriginalDueDate": "2023-09-28"
       |         },
       |         {
       |           "instalmentNumber": 2,
       |           "dueDate": "2024-11-28",
       |           "instalmentInterestAccrued": 29,
       |           "instalmentBalance": 748275,
       |           "debtItemChargeId": "A00000000001",
       |           "amountDue": 249425,
       |           "debtItemOriginalDueDate": "2023-09-28"
       |         },
       |         {
       |           "instalmentNumber": 1,
       |           "dueDate": "2024-10-28",
       |           "instalmentInterestAccrued": 29,
       |           "instalmentBalance": 997700,
       |           "debtItemChargeId": "A00000000001",
       |           "amountDue": 249425,
       |           "debtItemOriginalDueDate": "2023-09-28"
       |         }
       |       ]
       |     },
       |     {
       |       "planSelected": false,
       |       "planDuration": 0,
       |       "numberOfInstalments": 0,
       |       "totalDebt": 0,
       |       "totalDebtIncInt": 0,
       |       "planInterest": 0,
       |       "collections": {
       |         "regularCollections": []
       |       },
       |       "instalments": []
       |     }
       |   ]
       |  }
       |}
      |""".stripMargin
  )

  private def nextAlphanumericString(length: Int) = Random.alphanumeric.take(length).mkString("")

}
