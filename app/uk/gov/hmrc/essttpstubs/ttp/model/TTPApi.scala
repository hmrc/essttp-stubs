package uk.gov.hmrc.essttpstubs.ttp.model

import play.api.libs.json.{Format, Json}


/**
 * @param idType ID type
 * @param idNumber ID number
 * @param regimeType Regime type
 * @param processingDate Processing date
 * @param customerDetails
 * @param eligibilityStatus
 * @param eligibilityRules
 * @param financialLimitBreached
 * @param chargeTypeAssessment
 */
case class TTPEligibilityData(
                                                    idType: String,
                                                    idNumber: String,
                                                    regimeType: String,
                                                    processingDate: String,
                                                    customerDetails: CustomerDetails,
                                                    eligibilityStatus: EligibilityStatus,
                                                    eligibilityRules: EligibilityRules,
                                                    financialLimitBreached: FinancialLimitBreached,
                                                    chargeTypeAssessment: List[ChargeTypeAssessment]
                                                  )


/**
 * @param country Country
 * @param postCode Postcode
 */
case class CustomerDetails (
                             country: String,
                             postCode: String
                           )

object CustomerDetails{
   val fmt: Format[CustomerDetails] = Json.format[CustomerDetails]
}


/**
 * @param overallEligibilityStatus Overall Eligibility status
 * @param minPlanLengthMonths Minimumplan length in months
 * @param maxPlanLengthMonths Maximumplan length in months
 */
case class EligibilityStatus (
                               overallEligibilityStatus: Boolean,
                               minPlanLengthMonths: Int,
                               maxPlanLengthMonths: Int
                             )


/**
 * @param rlsOnAddress RLS on address
 * @param rlsReason RLS reason
 * @param markedAsInsolvent Is marked as insolvent
 * @param minimumDebtAllowance Minimum debth allowance
 * @param maxDebtAllowance Maximum debth allowance
 * @param disallowedChargeLock Disallowed Chargelock
 * @param existingTTP Have an existing TTP
 * @param minInstalmentAmount Minimum installment amount
 * @param maxInstalmentAmount Maximum installment amount
 * @param maxDebtAge Maximum debt age
 * @param eligibleChargeType Eligible Charge type
 * @param returnsFiled Returns filed
 */
case class EligibilityRules (
                              rlsOnAddress: Boolean,
                              rlsReason: String,
                              markedAsInsolvent: Boolean,
                              minimumDebtAllowance: Boolean,
                              maxDebtAllowance: Boolean,
                              disallowedChargeLock: Boolean,
                              existingTTP: Boolean,
                              minInstalmentAmount: Int,
                              maxInstalmentAmount: Int,
                              maxDebtAge: Boolean,
                              eligibleChargeType: Boolean,
                              returnsFiled: Boolean
                            )


/**
 * @param status Financial Limit breached status
 * @param calculatedAmount Calculated amount
 */
case class FinancialLimitBreached (
                                    status: Boolean,
                                    calculatedAmount: Int
                                  )


/**
 * @param taxPeriodFrom Tax period from date
 * @param taxPeriodTo Tax period to date
 * @param debtTotalAmount Total debt amount
 * @param taxPeriodCharges
 */
case class ChargeTypeAssessment (
                                  taxPeriodFrom: String,
                                  taxPeriodTo: String,
                                  debtTotalAmount: Int,
                                  taxPeriodCharges: List[TaxPeriodCharges]
                                )


/**
 * @param chargeId Charged id
 * @param mainTrans Main trans
 * @param mainTransDesc Main trans description
 * @param subTrans Sub trans
 * @param subTransDesc Sub trans description
 * @param outstandingDebtAmount Outstanding debt amount
 * @param interestStartDate Interest starting date
 * @param accruedInterestToDate Interest accrued to date
 * @param disallowedCharge Disallowed charge status
 * @param chargeLocks
 */
case class TaxPeriodCharges (
                              chargeId: String,
                              mainTrans: String,
                              mainTransDesc: String,
                              subTrans: String,
                              subTransDesc: String,
                              outstandingDebtAmount: Int,
                              interestStartDate: String,
                              accruedInterestToDate: Double,
                              disallowedCharge: Boolean,
                              chargeLocks: ChargeLocks
                            )


/**
 */
case class PaymentLock(
                        status: Boolean,
                        reason: String
                      )
case class ChargeLocks(
                        paymentLock: PaymentLock,
                        clearingLock: PaymentLock,
                        interestLock: PaymentLock,
                        dunningLock: PaymentLock,
                        disallowedLock: PaymentLock
                      )

