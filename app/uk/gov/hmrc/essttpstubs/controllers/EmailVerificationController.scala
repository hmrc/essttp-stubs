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

import com.google.inject.{Inject, Singleton}
import play.api.Logger
import play.api.libs.json.{JsValue, Json, Reads, Writes}
import play.api.mvc.{Action, ControllerComponents, Result}
import uk.gov.hmrc.play.bootstrap.backend.controller.BackendController

@Singleton
class EmailVerificationController @Inject() (cc: ControllerComponents) extends BackendController(cc) {

  import EmailVerificationController._

  val logger: Logger = Logger(this.getClass)

  val requestPasscode: Action[JsValue] = Action(parse.json) { request =>
    request.body
      .validate[PasscodeRequest]
      .fold(
        { e =>
          logger.warn(s"Could not parse passcode request: $e")
          BadRequest(ErrorResponse("VALIDATION_ERROR", s"json validation error $e").json)
        },
        { passcodeRequest =>
          val result = requestPasscodeResponse(passcodeRequest.email)
          logger.info(s"Received email passcode request $passcodeRequest. Responding with status ${result.header.status}")
          result
        }
      )

  }

  private def requestPasscodeResponse(email: String): Result = email match {
    case "no_session_id@email.com" =>
      Unauthorized(ErrorResponse("NO_SESSION_ID", "No session id provided").json)

    case "email_verified_already@email.com" =>
      Conflict(ErrorResponse("EMAIL_VERIFIED_ALREADY", "Email has already been verified").json)

    case "max_emails_exceeded@email.com" =>
      Forbidden(ErrorResponse("MAX_EMAILS_EXCEEDED", "Too many emails or email addresses").json)

    case "bad_email_request@email.com" =>
      BadRequest(
        ErrorResponse(
          "BAD_EMAIL_REQUEST",
          "email-verification had a problem, sendEmail returned bad request"
        ).json
      )

    case "upstream_error@email.com" =>
      BadGateway(
        ErrorResponse("UPSTREAM_ERROR", "email-verification had a problem, sendEmail returned not found").json
      )

    case _ =>
      Created
  }

  val verifyPasscode: Action[JsValue] = Action(parse.json) { request =>
    request.body
      .validate[PasscodeVerificationRequest]
      .fold(
        { e =>
          logger.warn(s"Could not parse passcode verification request: $e")
          BadRequest(ErrorResponse("VALIDATION_ERROR", s"json validation error $e").json)
        },
        { passcodeVerificationRequest =>
          val result = verifyPasscodeResponse(passcodeVerificationRequest.passcode)
          logger.info(s"Received verify passcode request $passcodeVerificationRequest. Responding with status ${result.header.status}")
          result
        }
      )
  }

  private def verifyPasscodeResponse(passcode: String): Result = passcode match {
    case "BBBBBB" =>
      Unauthorized(ErrorResponse("NO_SESSION_ID", "No session id provided").json)

    case "CCCCCC" =>
      Forbidden(ErrorResponse("MAX_PASSCODE_ATTEMPTS_EXCEEDED", "Too many attempts").json)

    case "DDDDDD" =>
      NotFound(ErrorResponse("PASSCODE_NOT_FOUND", "Passcode not found").json)

    case "FFFFFF" =>
      NotFound(ErrorResponse("PASSCODE_MISMATCH", "Passcode mismatch").json)

    case "GGGGGG" =>
      NoContent

    case _ =>
      Created
  }

}

object EmailVerificationController {

  final case class ErrorResponse(code: String, message: String)

  object ErrorResponse {

    implicit val writes: Writes[ErrorResponse] = Json.writes

    implicit class ErrorResponseOps(private val e: ErrorResponse) extends AnyVal {

      def json: JsValue = Json.toJson(e)

    }

  }

  final case class PasscodeRequest(email: String, serviceName: String, lang: String)

  object PasscodeRequest {

    implicit val reads: Reads[PasscodeRequest] = Json.reads
  }

  final case class PasscodeVerificationRequest(passcode: String, email: String)

  object PasscodeVerificationRequest {

    implicit val reads: Reads[PasscodeVerificationRequest] = Json.reads

  }

}

