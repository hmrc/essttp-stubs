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

import play.api.libs.json.{JsDefined, JsObject, JsString, JsValue, Json}
import play.api.test.Helpers._
import uk.gov.hmrc.essttpstubs.testutil.ItSpec
import uk.gov.hmrc.essttpstubs.testutil.connector.TestEmailVerificationConnector
import uk.gov.hmrc.http.UpstreamErrorResponse

class EmailVerificationControllerSpec extends ItSpec {

  lazy val connector: TestEmailVerificationConnector = injector.instanceOf[TestEmailVerificationConnector]

  def checkErrorCode(result: UpstreamErrorResponse, expectedErrorCode: String) = {
    val regex = ".*Response body: '(.*)'".r
    val jsonBody = result.message match {
      case regex(json) => json
    }

    (Json.parse(jsonBody).as[JsObject] \ "code") shouldBe JsDefined(JsString(expectedErrorCode))
  }

  "EmailVerificationController" - {

    "when handling requests to request a passcode must" - {

        def getRequestPasscodeErrorResponse(json: JsValue) =
          asUpstreamErrorResponse(connector.requestPasscode(json).failed.futureValue)

        def validJsonBody(email: String): JsValue = Json.parse(
          s"""{ "email": "$email", "serviceName": "service", "lang": "en" }""".stripMargin
        )

      "return a 400 (bad request) when" - {

        "the json body cannot be parsed" in {
          val result = getRequestPasscodeErrorResponse(JsString("hi"))
          result.statusCode shouldBe BAD_REQUEST
          checkErrorCode(result, "VALIDATION_ERROR")
        }

        "the email is 'bad_email_request@email.com'" in {
          val result = getRequestPasscodeErrorResponse(validJsonBody("bad_email_request@email.com"))

          result.statusCode shouldBe BAD_REQUEST
          checkErrorCode(result, "BAD_EMAIL_REQUEST")
        }

      }

      "return a 401 (unauthorised) when the email is 'no_session_id@email.com'" in {
        val result = getRequestPasscodeErrorResponse(validJsonBody("no_session_id@email.com"))

        result.statusCode shouldBe UNAUTHORIZED
        checkErrorCode(result, "NO_SESSION_ID")
      }

      "return a 409 (conflict) when the email is 'email_verified_already@email.com'" in {
        val result = getRequestPasscodeErrorResponse(validJsonBody("email_verified_already@email.com"))

        result.statusCode shouldBe CONFLICT
        checkErrorCode(result, "EMAIL_VERIFIED_ALREADY")
      }

      "return a 403 (forbidden) when the email is 'max_emails_exceeded@email.com'" in {
        val result = getRequestPasscodeErrorResponse(validJsonBody("max_emails_exceeded@email.com"))

        result.statusCode shouldBe FORBIDDEN
        checkErrorCode(result, "MAX_EMAILS_EXCEEDED")
      }

      "return a 502 (bad gateway) when the email is 'upstream_error@email.com'" in {
        val result = getRequestPasscodeErrorResponse(validJsonBody("upstream_error@email.com"))

        result.statusCode shouldBe BAD_GATEWAY
        checkErrorCode(result, "UPSTREAM_ERROR")
      }

      "return a 201 (created) otherwise" in {
        val result = connector.requestPasscode(validJsonBody("test@email.com")).futureValue
        result.status shouldBe CREATED
      }

    }

    "handling requests to verify a passcode must" - {

        def getVerifyPasscodeErrorResponse(json: JsValue) =
          asUpstreamErrorResponse(connector.verifyPasscode(json).failed.futureValue)

        def validJsonBody(passcode: String) = Json.parse(
          s"""{ "passcode": "$passcode", "email": "email" }""".stripMargin
        )

      "return a 400 (bad request) when the json body cannot be parsed" in {
        val result = getVerifyPasscodeErrorResponse(JsString("hi"))

        result.statusCode shouldBe BAD_REQUEST
        checkErrorCode(result, "VALIDATION_ERROR")
      }

      "return a 401 (unauthorised) when the passcode is 'BBBBBB'" in {
        val result = getVerifyPasscodeErrorResponse(validJsonBody("BBBBBB"))

        result.statusCode shouldBe UNAUTHORIZED
        checkErrorCode(result, "NO_SESSION_ID")
      }

      "return a 403 (forbidden) when the passcode is 'CCCCCC'" in {
        val result = getVerifyPasscodeErrorResponse(validJsonBody("CCCCCC"))

        result.statusCode shouldBe FORBIDDEN
        checkErrorCode(result, "MAX_PASSCODE_ATTEMPTS_EXCEEDED")
      }

      "return a 404 (not found) when" - {

        "the passcode is 'DDDDDD'" in {
          val result = getVerifyPasscodeErrorResponse(validJsonBody("DDDDDD"))

          result.statusCode shouldBe NOT_FOUND
          checkErrorCode(result, "PASSCODE_NOT_FOUND")
        }

        "the passcode is 'FFFFFF'" in {
          val result = getVerifyPasscodeErrorResponse(validJsonBody("FFFFFF"))

          result.statusCode shouldBe NOT_FOUND
          checkErrorCode(result, "PASSCODE_MISMATCH")
        }

      }

      "return a 204 (no content) when the passcode is 'GGGGGG'" in {
        val result = connector.verifyPasscode(validJsonBody("GGGGGG")).futureValue
        result.status shouldBe NO_CONTENT
      }

      "return a 201 (created) otherwise" in {
        val result = connector.verifyPasscode(validJsonBody("BCDFGH")).futureValue
        result.status shouldBe CREATED
      }

    }

  }

}
