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

package uk.gov.hmrc.essttpstubs.testutil

import essttp.rootmodel.ttp._
import essttp.rootmodel.ttp.affordablequotes.DueDate
import essttp.rootmodel.AmountInPence
import essttp.rootmodel.ttp.eligibility._
import play.api.libs.json.{JsValue, Json}
import uk.gov.hmrc.crypto.Sensitive.SensitiveString
import uk.gov.hmrc.essttpstubs.model.EligibilityRequest

import java.time.LocalDate

object TestData {

  object EligibilityApi {

    object ModelInstances {

      val testLock: Lock = Lock(
        lockType                 = LockType("testLockType"),
        lockReason               = LockReason("testLockReason"),
        disallowedChargeLockType = DisallowedChargeLockType(value = false)
      )

      val eligibilityRequest: EligibilityRequest = EligibilityRequest(
        channelIdentifier         = "eSSTTP",
        identification            = List(Identification(IdType("EMPREF"), IdValue("test-idValue"))),
        regimeType                = "test-regimeType",
        returnFinancialAssessment = true
      )

      val eligibilityResponse: EligibilityCheckResult = EligibilityCheckResult(
        processingDateTime              = ProcessingDateTime("test-processingDate"),
        identification                  = List(
          Identification(
            idType  = IdType("EMPREF"),
            idValue = IdValue("test-idValue")
          )
        ),
        customerPostcodes               = List(
          CustomerPostcode(addressPostcode = Postcode(SensitiveString("test-postcode")), postcodeDate = PostcodeDate("2022-01-01"))
        ),
        regimePaymentFrequency          = PaymentPlanFrequencies.Monthly,
        paymentPlanFrequency            = PaymentPlanFrequencies.Monthly,
        paymentPlanMinLength            = PaymentPlanMinLength(1),
        paymentPlanMaxLength            = PaymentPlanMaxLength(6),
        eligibilityStatus               = EligibilityStatus(EligibilityPass(value = false)),
        eligibilityRules                = EligibilityRules(
          hasRlsOnAddress                       = true,
          markedAsInsolvent                     = true,
          isLessThanMinDebtAllowance            = false,
          isMoreThanMaxDebtAllowance            = false,
          disallowedChargeLockTypes             = false,
          existingTTP                           = false,
          chargesOverMaxDebtAge                 = None,
          ineligibleChargeTypes                 = false,
          missingFiledReturns                   = false,
          hasInvalidInterestSignals             = None,
          dmSpecialOfficeProcessingRequired     = None,
          noDueDatesReached                     = false,
          cannotFindLockReason                  = None,
          creditsNotAllowed                     = None,
          isMoreThanMaxPaymentReference         = None,
          chargesBeforeMaxAccountingDate        = None,
          hasInvalidInterestSignalsCESA         = None,
          hasDisguisedRemuneration              = None,
          hasCapacitor                          = None,
          dmSpecialOfficeProcessingRequiredCDCS = None
        ),
        chargeTypeAssessment            = List(
          ChargeTypeAssessment(
            taxPeriodFrom   = TaxPeriodFrom("2022-04-27"),
            taxPeriodTo     = TaxPeriodTo("2022-04-27"),
            debtTotalAmount = DebtTotalAmount(AmountInPence(100)),
            chargeReference = ChargeReference("test-chargeReference"),
            charges         = List(
              Charges(
                chargeType                    = ChargeType("test-chargeId"),
                mainTrans                     = MainTrans("test-mainTrans"),
                subTrans                      = SubTrans("test-subTrans"),
                outstandingAmount             = OutstandingAmount(AmountInPence(10)),
                interestStartDate             = Some(InterestStartDate(LocalDate.parse("2022-04-27"))),
                accruedInterest               = AccruedInterest(AmountInPence(1)),
                mainType                      = MainType("test-mainType"),
                dueDate                       = DueDate(LocalDate.parse("2022-04-27")),
                ineligibleChargeType          = IneligibleChargeType(value = false),
                chargeOverMaxDebtAge          = Some(ChargeOverMaxDebtAge(value = false)),
                locks                         = Some(List(testLock)),
                dueDateNotReached             = false,
                isInterestBearingCharge       = None,
                useChargeReference            = None,
                chargeBeforeMaxAccountingDate = None,
                ddInProgress                  = None
              )
            )
          )
        ),
        customerDetails                 = None,
        regimeDigitalCorrespondence     = None,
        futureChargeLiabilitiesExcluded = false,
        chargeTypesExcluded             = None,
        invalidSignals                  = None,
        customerType                    = None
      )
    }

    object JsonInstances {
      val eligibilityRequestJson: JsValue = Json.parse(
        //language=JSON
        """{
          |   "channelIdentifier":"eSSTTP",
          |   "idType": "test-idType",
          |   "idValue": "test-idValue",
          |   "regimeType": "test-regimeType",
          |   "returnFinancialAssessment": true
          |}
          |""".stripMargin
      )
      val eligibilityResponseJson: JsValue = Json.parse(
        //language=JSON
        """{
          |	"processingDateTime": "test-processingDate",
          |	"identification": [{
          |		"idType": "EMPREF",
          |		"idValue": "test-idValue"
          |	}],
          |	"customerPostcodes": [{
          |		"addressPostcode": "test-postcode",
          |		"postcodeDate": "2022-01-01"
          |	}],
          |	"regimePaymentFrequency": "Monthly",
          |	"paymentPlanFrequency": "Monthly",
          |	"paymentPlanMinLength": 1,
          |	"paymentPlanMaxLength": 6,
          |	"eligibilityStatus": {
          |		"eligibilityPass": false
          |	},
          |	"eligibilityRules": {
          |		"hasRlsOnAddress": true,
          |		"markedAsInsolvent": true,
          |		"isLessThanMinDebtAllowance": false,
          |		"isMoreThanMaxDebtAllowance": false,
          |		"disallowedChargeLockTypes": false,
          |		"existingTTP": false,
          |		"ineligibleChargeTypes": false,
          |		"missingFiledReturns": false,
          |   "noDueDatesReached": false
          |	},
          |	"chargeTypeAssessment": [{
          |		"taxPeriodFrom": "2022-04-27",
          |		"taxPeriodTo": "2022-04-27",
          |		"debtTotalAmount": 100,
          |   "chargeReference": "test-chargeReference",
          |		"charges": [{
          |			"chargeType": "test-chargeId",
          |			"mainType": "test-mainType",
          |			"mainTrans": "test-mainTrans",
          |			"subTrans": "test-subTrans",
          |			"outstandingAmount": 10,
          |			"interestStartDate": "2022-04-27",
          |			"dueDate": "2022-04-27",
          |			"accruedInterest": 1,
          |			"ineligibleChargeType": false,
          |			"chargeOverMaxDebtAge": false,
          |			"locks": [{
          |				"lockType": "testLockType",
          |				"lockReason": "testLockReason",
          |				"disallowedChargeLockType": false
          |		  	}],
          |     "dueDateNotReached": false
          |	  	}]
          |	  }],
          |  "futureChargeLiabilitiesExcluded": false
          |}""".stripMargin
      )
    }
  }

}
