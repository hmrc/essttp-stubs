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

import play.api.libs.json.{JsObject, Json}

object TdAll {
  object AffordabilityJsonBodies {
    def `1-2-3`: JsObject =
      Json.parse(
        """
          |{
          |	"paymentPlans": [{
          |		"numberOfInstalments": 1,
          |		"planDuration": 1,
          |		"totalDebt": 600000,
          |		"totalDebtIncInt": 600018,
          |		"planInterest": 18,
          |		"collections": {
          |			"regularCollections": [{
          |				"dueDate": "2022-08-08",
          |				"amountDue": 600018
          |			}]
          |		},
          |		"instalments": [{
          |			"instalmentNumber": 1,
          |			"dueDate": "2022-08-08",
          |			"instalmentInterestAccrued": 18,
          |			"instalmentBalance": 600000,
          |			"debtItemChargeId": "XW006559808862",
          |			"amountDue": 600000,
          |			"debtItemOriginalDueDate": "2021-07-08"
          |		}]
          |	}, {
          |		"numberOfInstalments": 2,
          |		"planDuration": 2,
          |		"totalDebt": 600000,
          |		"totalDebtIncInt": 600036,
          |		"planInterest": 36,
          |		"collections": {
          |			"regularCollections": [{
          |				"dueDate": "2022-08-08",
          |				"amountDue": 300018
          |			}, {
          |				"dueDate": "2022-09-08",
          |				"amountDue": 300018
          |			}]
          |		},
          |		"instalments": [{
          |			"instalmentNumber": 2,
          |			"dueDate": "2022-09-08",
          |			"instalmentInterestAccrued": 18,
          |			"instalmentBalance": 300000,
          |			"debtItemChargeId": "XW006559808862",
          |			"amountDue": 300000,
          |			"debtItemOriginalDueDate": "2021-07-08"
          |		}, {
          |			"instalmentNumber": 1,
          |			"dueDate": "2022-08-08",
          |			"instalmentInterestAccrued": 18,
          |			"instalmentBalance": 600000,
          |			"debtItemChargeId": "XW006559808862",
          |			"amountDue": 300000,
          |			"debtItemOriginalDueDate": "2021-07-08"
          |		}]
          |	}, {
          |		"numberOfInstalments": 3,
          |		"planDuration": 3,
          |		"totalDebt": 600000,
          |		"totalDebtIncInt": 600054,
          |		"planInterest": 54,
          |		"collections": {
          |			"regularCollections": [{
          |				"dueDate": "2022-08-08",
          |				"amountDue": 200018
          |			}, {
          |				"dueDate": "2022-09-08",
          |				"amountDue": 200018
          |			}, {
          |				"dueDate": "2022-10-08",
          |				"amountDue": 200018
          |			}]
          |		},
          |		"instalments": [{
          |			"instalmentNumber": 3,
          |			"dueDate": "2022-10-08",
          |			"instalmentInterestAccrued": 18,
          |			"instalmentBalance": 200000,
          |			"debtItemChargeId": "XW006559808862",
          |			"amountDue": 200000,
          |			"debtItemOriginalDueDate": "2021-07-08"
          |		}, {
          |			"instalmentNumber": 2,
          |			"dueDate": "2022-09-08",
          |			"instalmentInterestAccrued": 18,
          |			"instalmentBalance": 400000,
          |			"debtItemChargeId": "XW006559808862",
          |			"amountDue": 200000,
          |			"debtItemOriginalDueDate": "2021-07-08"
          |		}, {
          |			"instalmentNumber": 1,
          |			"dueDate": "2022-08-08",
          |			"instalmentInterestAccrued": 18,
          |			"instalmentBalance": 600000,
          |			"debtItemChargeId": "XW006559808862",
          |			"amountDue": 200000,
          |			"debtItemOriginalDueDate": "2021-07-08"
          |		}]
          |	}]
          |}
          |""".stripMargin
      ).as[JsObject]

    def `2-3-4`: JsObject =
      Json.parse(
        """
          |{
          |	"paymentPlans": [{
          |		"numberOfInstalments": 2,
          |		"planDuration": 2,
          |		"totalDebt": 600000,
          |		"totalDebtIncInt": 600036,
          |		"planInterest": 36,
          |		"collections": {
          |			"regularCollections": [{
          |				"dueDate": "2022-08-08",
          |				"amountDue": 300018
          |			}, {
          |				"dueDate": "2022-09-08",
          |				"amountDue": 300018
          |			}]
          |		},
          |		"instalments": [{
          |			"instalmentNumber": 2,
          |			"dueDate": "2022-09-08",
          |			"instalmentInterestAccrued": 18,
          |			"instalmentBalance": 300000,
          |			"debtItemChargeId": "XW006559808862",
          |			"amountDue": 300000,
          |			"debtItemOriginalDueDate": "2021-07-08"
          |		}, {
          |			"instalmentNumber": 1,
          |			"dueDate": "2022-08-08",
          |			"instalmentInterestAccrued": 18,
          |			"instalmentBalance": 600000,
          |			"debtItemChargeId": "XW006559808862",
          |			"amountDue": 300000,
          |			"debtItemOriginalDueDate": "2021-07-08"
          |		}]
          |	}, {
          |		"numberOfInstalments": 3,
          |		"planDuration": 3,
          |		"totalDebt": 600000,
          |		"totalDebtIncInt": 600054,
          |		"planInterest": 54,
          |		"collections": {
          |			"regularCollections": [{
          |				"dueDate": "2022-08-08",
          |				"amountDue": 200018
          |			}, {
          |				"dueDate": "2022-09-08",
          |				"amountDue": 200018
          |			}, {
          |				"dueDate": "2022-10-08",
          |				"amountDue": 200018
          |			}]
          |		},
          |		"instalments": [{
          |			"instalmentNumber": 3,
          |			"dueDate": "2022-10-08",
          |			"instalmentInterestAccrued": 18,
          |			"instalmentBalance": 200000,
          |			"debtItemChargeId": "XW006559808862",
          |			"amountDue": 200000,
          |			"debtItemOriginalDueDate": "2021-07-08"
          |		}, {
          |			"instalmentNumber": 2,
          |			"dueDate": "2022-09-08",
          |			"instalmentInterestAccrued": 18,
          |			"instalmentBalance": 400000,
          |			"debtItemChargeId": "XW006559808862",
          |			"amountDue": 200000,
          |			"debtItemOriginalDueDate": "2021-07-08"
          |		}, {
          |			"instalmentNumber": 1,
          |			"dueDate": "2022-08-08",
          |			"instalmentInterestAccrued": 18,
          |			"instalmentBalance": 600000,
          |			"debtItemChargeId": "XW006559808862",
          |			"amountDue": 200000,
          |			"debtItemOriginalDueDate": "2021-07-08"
          |		}]
          |	}, {
          |		"numberOfInstalments": 4,
          |		"planDuration": 4,
          |		"totalDebt": 600000,
          |		"totalDebtIncInt": 600072,
          |		"planInterest": 72,
          |		"collections": {
          |			"regularCollections": [{
          |				"dueDate": "2022-08-08",
          |				"amountDue": 150018
          |			}, {
          |				"dueDate": "2022-09-08",
          |				"amountDue": 150018
          |			}, {
          |				"dueDate": "2022-10-08",
          |				"amountDue": 150018
          |			}, {
          |				"dueDate": "2022-11-08",
          |				"amountDue": 150018
          |			}]
          |		},
          |		"instalments": [{
          |			"instalmentNumber": 4,
          |			"dueDate": "2022-11-08",
          |			"instalmentInterestAccrued": 18,
          |			"instalmentBalance": 150000,
          |			"debtItemChargeId": "XW006559808862",
          |			"amountDue": 150000,
          |			"debtItemOriginalDueDate": "2021-07-08"
          |		}, {
          |			"instalmentNumber": 3,
          |			"dueDate": "2022-10-08",
          |			"instalmentInterestAccrued": 18,
          |			"instalmentBalance": 300000,
          |			"debtItemChargeId": "XW006559808862",
          |			"amountDue": 150000,
          |			"debtItemOriginalDueDate": "2021-07-08"
          |		}, {
          |			"instalmentNumber": 2,
          |			"dueDate": "2022-09-08",
          |			"instalmentInterestAccrued": 18,
          |			"instalmentBalance": 450000,
          |			"debtItemChargeId": "XW006559808862",
          |			"amountDue": 150000,
          |			"debtItemOriginalDueDate": "2021-07-08"
          |		}, {
          |			"instalmentNumber": 1,
          |			"dueDate": "2022-08-08",
          |			"instalmentInterestAccrued": 18,
          |			"instalmentBalance": 600000,
          |			"debtItemChargeId": "XW006559808862",
          |			"amountDue": 150000,
          |			"debtItemOriginalDueDate": "2021-07-08"
          |		}]
          |	}]
          |}
          |""".stripMargin
      ).as[JsObject]

    def `3-4-5`: JsObject =
      Json.parse(
        """
          |{
          |	"paymentPlans": [{
          |		"numberOfInstalments": 3,
          |		"planDuration": 3,
          |		"totalDebt": 600000,
          |		"totalDebtIncInt": 600054,
          |		"planInterest": 54,
          |		"collections": {
          |			"regularCollections": [{
          |				"dueDate": "2022-08-08",
          |				"amountDue": 200018
          |			}, {
          |				"dueDate": "2022-09-08",
          |				"amountDue": 200018
          |			}, {
          |				"dueDate": "2022-10-08",
          |				"amountDue": 200018
          |			}]
          |		},
          |		"instalments": [{
          |			"instalmentNumber": 3,
          |			"dueDate": "2022-10-08",
          |			"instalmentInterestAccrued": 18,
          |			"instalmentBalance": 200000,
          |			"debtItemChargeId": "XW006559808862",
          |			"amountDue": 200000,
          |			"debtItemOriginalDueDate": "2021-07-08"
          |		}, {
          |			"instalmentNumber": 2,
          |			"dueDate": "2022-09-08",
          |			"instalmentInterestAccrued": 18,
          |			"instalmentBalance": 400000,
          |			"debtItemChargeId": "XW006559808862",
          |			"amountDue": 200000,
          |			"debtItemOriginalDueDate": "2021-07-08"
          |		}, {
          |			"instalmentNumber": 1,
          |			"dueDate": "2022-08-08",
          |			"instalmentInterestAccrued": 18,
          |			"instalmentBalance": 600000,
          |			"debtItemChargeId": "XW006559808862",
          |			"amountDue": 200000,
          |			"debtItemOriginalDueDate": "2021-07-08"
          |		}]
          |	}, {
          |		"numberOfInstalments": 4,
          |		"planDuration": 4,
          |		"totalDebt": 600000,
          |		"totalDebtIncInt": 600072,
          |		"planInterest": 72,
          |		"collections": {
          |			"regularCollections": [{
          |				"dueDate": "2022-08-08",
          |				"amountDue": 150018
          |			}, {
          |				"dueDate": "2022-09-08",
          |				"amountDue": 150018
          |			}, {
          |				"dueDate": "2022-10-08",
          |				"amountDue": 150018
          |			}, {
          |				"dueDate": "2022-11-08",
          |				"amountDue": 150018
          |			}]
          |		},
          |		"instalments": [{
          |			"instalmentNumber": 4,
          |			"dueDate": "2022-11-08",
          |			"instalmentInterestAccrued": 18,
          |			"instalmentBalance": 150000,
          |			"debtItemChargeId": "XW006559808862",
          |			"amountDue": 150000,
          |			"debtItemOriginalDueDate": "2021-07-08"
          |		}, {
          |			"instalmentNumber": 3,
          |			"dueDate": "2022-10-08",
          |			"instalmentInterestAccrued": 18,
          |			"instalmentBalance": 300000,
          |			"debtItemChargeId": "XW006559808862",
          |			"amountDue": 150000,
          |			"debtItemOriginalDueDate": "2021-07-08"
          |		}, {
          |			"instalmentNumber": 2,
          |			"dueDate": "2022-09-08",
          |			"instalmentInterestAccrued": 18,
          |			"instalmentBalance": 450000,
          |			"debtItemChargeId": "XW006559808862",
          |			"amountDue": 150000,
          |			"debtItemOriginalDueDate": "2021-07-08"
          |		}, {
          |			"instalmentNumber": 1,
          |			"dueDate": "2022-08-08",
          |			"instalmentInterestAccrued": 18,
          |			"instalmentBalance": 600000,
          |			"debtItemChargeId": "XW006559808862",
          |			"amountDue": 150000,
          |			"debtItemOriginalDueDate": "2021-07-08"
          |		}]
          |	}, {
          |		"numberOfInstalments": 5,
          |		"planDuration": 5,
          |		"totalDebt": 600000,
          |		"totalDebtIncInt": 600090,
          |		"planInterest": 90,
          |		"collections": {
          |			"regularCollections": [{
          |				"dueDate": "2022-08-08",
          |				"amountDue": 120018
          |			}, {
          |				"dueDate": "2022-09-08",
          |				"amountDue": 120018
          |			}, {
          |				"dueDate": "2022-10-08",
          |				"amountDue": 120018
          |			}, {
          |				"dueDate": "2022-11-08",
          |				"amountDue": 120018
          |			}, {
          |				"dueDate": "2022-12-08",
          |				"amountDue": 120018
          |			}]
          |		},
          |		"instalments": [{
          |			"instalmentNumber": 5,
          |			"dueDate": "2022-12-08",
          |			"instalmentInterestAccrued": 18,
          |			"instalmentBalance": 120000,
          |			"debtItemChargeId": "XW006559808862",
          |			"amountDue": 120000,
          |			"debtItemOriginalDueDate": "2021-07-08"
          |		}, {
          |			"instalmentNumber": 4,
          |			"dueDate": "2022-11-08",
          |			"instalmentInterestAccrued": 18,
          |			"instalmentBalance": 240000,
          |			"debtItemChargeId": "XW006559808862",
          |			"amountDue": 120000,
          |			"debtItemOriginalDueDate": "2021-07-08"
          |		}, {
          |			"instalmentNumber": 3,
          |			"dueDate": "2022-10-08",
          |			"instalmentInterestAccrued": 18,
          |			"instalmentBalance": 360000,
          |			"debtItemChargeId": "XW006559808862",
          |			"amountDue": 120000,
          |			"debtItemOriginalDueDate": "2021-07-08"
          |		}, {
          |			"instalmentNumber": 2,
          |			"dueDate": "2022-09-08",
          |			"instalmentInterestAccrued": 18,
          |			"instalmentBalance": 480000,
          |			"debtItemChargeId": "XW006559808862",
          |			"amountDue": 120000,
          |			"debtItemOriginalDueDate": "2021-07-08"
          |		}, {
          |			"instalmentNumber": 1,
          |			"dueDate": "2022-08-08",
          |			"instalmentInterestAccrued": 18,
          |			"instalmentBalance": 600000,
          |			"debtItemChargeId": "XW006559808862",
          |			"amountDue": 120000,
          |			"debtItemOriginalDueDate": "2021-07-08"
          |		}]
          |	}]
          |}
          |""".stripMargin
      ).as[JsObject]

    def `4-5-6`: JsObject =
      Json.parse(
        """
          |{
          |	"paymentPlans": [{
          |		"numberOfInstalments": 4,
          |		"planDuration": 4,
          |		"totalDebt": 600000,
          |		"totalDebtIncInt": 600072,
          |		"planInterest": 72,
          |		"collections": {
          |			"regularCollections": [{
          |				"dueDate": "2022-08-08",
          |				"amountDue": 150018
          |			}, {
          |				"dueDate": "2022-09-08",
          |				"amountDue": 150018
          |			}, {
          |				"dueDate": "2022-10-08",
          |				"amountDue": 150018
          |			}, {
          |				"dueDate": "2022-11-08",
          |				"amountDue": 150018
          |			}]
          |		},
          |		"instalments": [{
          |			"instalmentNumber": 4,
          |			"dueDate": "2022-11-08",
          |			"instalmentInterestAccrued": 18,
          |			"instalmentBalance": 150000,
          |			"debtItemChargeId": "XW006559808862",
          |			"amountDue": 150000,
          |			"debtItemOriginalDueDate": "2021-07-08"
          |		}, {
          |			"instalmentNumber": 3,
          |			"dueDate": "2022-10-08",
          |			"instalmentInterestAccrued": 18,
          |			"instalmentBalance": 300000,
          |			"debtItemChargeId": "XW006559808862",
          |			"amountDue": 150000,
          |			"debtItemOriginalDueDate": "2021-07-08"
          |		}, {
          |			"instalmentNumber": 2,
          |			"dueDate": "2022-09-08",
          |			"instalmentInterestAccrued": 18,
          |			"instalmentBalance": 450000,
          |			"debtItemChargeId": "XW006559808862",
          |			"amountDue": 150000,
          |			"debtItemOriginalDueDate": "2021-07-08"
          |		}, {
          |			"instalmentNumber": 1,
          |			"dueDate": "2022-08-08",
          |			"instalmentInterestAccrued": 18,
          |			"instalmentBalance": 600000,
          |			"debtItemChargeId": "XW006559808862",
          |			"amountDue": 150000,
          |			"debtItemOriginalDueDate": "2021-07-08"
          |		}]
          |	}, {
          |		"numberOfInstalments": 5,
          |		"planDuration": 5,
          |		"totalDebt": 600000,
          |		"totalDebtIncInt": 600090,
          |		"planInterest": 90,
          |		"collections": {
          |			"regularCollections": [{
          |				"dueDate": "2022-08-08",
          |				"amountDue": 120018
          |			}, {
          |				"dueDate": "2022-09-08",
          |				"amountDue": 120018
          |			}, {
          |				"dueDate": "2022-10-08",
          |				"amountDue": 120018
          |			}, {
          |				"dueDate": "2022-11-08",
          |				"amountDue": 120018
          |			}, {
          |				"dueDate": "2022-12-08",
          |				"amountDue": 120018
          |			}]
          |		},
          |		"instalments": [{
          |			"instalmentNumber": 5,
          |			"dueDate": "2022-12-08",
          |			"instalmentInterestAccrued": 18,
          |			"instalmentBalance": 120000,
          |			"debtItemChargeId": "XW006559808862",
          |			"amountDue": 120000,
          |			"debtItemOriginalDueDate": "2021-07-08"
          |		}, {
          |			"instalmentNumber": 4,
          |			"dueDate": "2022-11-08",
          |			"instalmentInterestAccrued": 18,
          |			"instalmentBalance": 240000,
          |			"debtItemChargeId": "XW006559808862",
          |			"amountDue": 120000,
          |			"debtItemOriginalDueDate": "2021-07-08"
          |		}, {
          |			"instalmentNumber": 3,
          |			"dueDate": "2022-10-08",
          |			"instalmentInterestAccrued": 18,
          |			"instalmentBalance": 360000,
          |			"debtItemChargeId": "XW006559808862",
          |			"amountDue": 120000,
          |			"debtItemOriginalDueDate": "2021-07-08"
          |		}, {
          |			"instalmentNumber": 2,
          |			"dueDate": "2022-09-08",
          |			"instalmentInterestAccrued": 18,
          |			"instalmentBalance": 480000,
          |			"debtItemChargeId": "XW006559808862",
          |			"amountDue": 120000,
          |			"debtItemOriginalDueDate": "2021-07-08"
          |		}, {
          |			"instalmentNumber": 1,
          |			"dueDate": "2022-08-08",
          |			"instalmentInterestAccrued": 18,
          |			"instalmentBalance": 600000,
          |			"debtItemChargeId": "XW006559808862",
          |			"amountDue": 120000,
          |			"debtItemOriginalDueDate": "2021-07-08"
          |		}]
          |	}, {
          |		"numberOfInstalments": 6,
          |		"planDuration": 6,
          |		"totalDebt": 600000,
          |		"totalDebtIncInt": 600108,
          |		"planInterest": 108,
          |		"collections": {
          |			"regularCollections": [{
          |				"dueDate": "2022-08-08",
          |				"amountDue": 100018
          |			}, {
          |				"dueDate": "2022-09-08",
          |				"amountDue": 100018
          |			}, {
          |				"dueDate": "2022-10-08",
          |				"amountDue": 100018
          |			}, {
          |				"dueDate": "2022-11-08",
          |				"amountDue": 100018
          |			}, {
          |				"dueDate": "2022-12-08",
          |				"amountDue": 100018
          |			}, {
          |				"dueDate": "2023-01-08",
          |				"amountDue": 100018
          |			}]
          |		},
          |		"instalments": [{
          |			"instalmentNumber": 6,
          |			"dueDate": "2023-01-08",
          |			"instalmentInterestAccrued": 18,
          |			"instalmentBalance": 100000,
          |			"debtItemChargeId": "XW006559808862",
          |			"amountDue": 100000,
          |			"debtItemOriginalDueDate": "2021-07-08"
          |		}, {
          |			"instalmentNumber": 5,
          |			"dueDate": "2022-12-08",
          |			"instalmentInterestAccrued": 18,
          |			"instalmentBalance": 200000,
          |			"debtItemChargeId": "XW006559808862",
          |			"amountDue": 100000,
          |			"debtItemOriginalDueDate": "2021-07-08"
          |		}, {
          |			"instalmentNumber": 4,
          |			"dueDate": "2022-11-08",
          |			"instalmentInterestAccrued": 18,
          |			"instalmentBalance": 300000,
          |			"debtItemChargeId": "XW006559808862",
          |			"amountDue": 100000,
          |			"debtItemOriginalDueDate": "2021-07-08"
          |		}, {
          |			"instalmentNumber": 3,
          |			"dueDate": "2022-10-08",
          |			"instalmentInterestAccrued": 18,
          |			"instalmentBalance": 400000,
          |			"debtItemChargeId": "XW006559808862",
          |			"amountDue": 100000,
          |			"debtItemOriginalDueDate": "2021-07-08"
          |		}, {
          |			"instalmentNumber": 2,
          |			"dueDate": "2022-09-08",
          |			"instalmentInterestAccrued": 18,
          |			"instalmentBalance": 500000,
          |			"debtItemChargeId": "XW006559808862",
          |			"amountDue": 100000,
          |			"debtItemOriginalDueDate": "2021-07-08"
          |		}, {
          |			"instalmentNumber": 1,
          |			"dueDate": "2022-08-08",
          |			"instalmentInterestAccrued": 18,
          |			"instalmentBalance": 600000,
          |			"debtItemChargeId": "XW006559808862",
          |			"amountDue": 100000,
          |			"debtItemOriginalDueDate": "2021-07-08"
          |		}]
          |	}]
          |}
          |""".stripMargin
      ).as[JsObject]

    def `4-5-6-withUpfrontPayment`: JsObject =
      Json.parse(
        """
          |{
          |	"paymentPlans": [{
          |		"numberOfInstalments": 4,
          |		"planDuration": 4,
          |		"totalDebt": 599000,
          |		"totalDebtIncInt": 599068,
          |		"planInterest": 68,
          |		"collections": {
          |			"initialCollection": {
          |				"dueDate": "2022-06-18",
          |				"amountDue": 1000
          |			},
          |			"regularCollections": [{
          |				"dueDate": "2022-08-08",
          |				"amountDue": 149767
          |			}, {
          |				"dueDate": "2022-09-08",
          |				"amountDue": 149767
          |			}, {
          |				"dueDate": "2022-10-08",
          |				"amountDue": 149767
          |			}, {
          |				"dueDate": "2022-11-08",
          |				"amountDue": 149767
          |			}]
          |		},
          |		"instalments": [{
          |			"instalmentNumber": 4,
          |			"dueDate": "2022-11-08",
          |			"instalmentInterestAccrued": 17,
          |			"instalmentBalance": 149750,
          |			"debtItemChargeId": "XW006559808862",
          |			"amountDue": 149750,
          |			"debtItemOriginalDueDate": "2021-07-08"
          |		}, {
          |			"instalmentNumber": 3,
          |			"dueDate": "2022-10-08",
          |			"instalmentInterestAccrued": 17,
          |			"instalmentBalance": 299500,
          |			"debtItemChargeId": "XW006559808862",
          |			"amountDue": 149750,
          |			"debtItemOriginalDueDate": "2021-07-08"
          |		}, {
          |			"instalmentNumber": 2,
          |			"dueDate": "2022-09-08",
          |			"instalmentInterestAccrued": 17,
          |			"instalmentBalance": 449250,
          |			"debtItemChargeId": "XW006559808862",
          |			"amountDue": 149750,
          |			"debtItemOriginalDueDate": "2021-07-08"
          |		}, {
          |			"instalmentNumber": 1,
          |			"dueDate": "2022-08-08",
          |			"instalmentInterestAccrued": 17,
          |			"instalmentBalance": 599000,
          |			"debtItemChargeId": "XW006559808862",
          |			"amountDue": 149750,
          |			"debtItemOriginalDueDate": "2021-07-08"
          |		}]
          |	}, {
          |		"numberOfInstalments": 5,
          |		"planDuration": 5,
          |		"totalDebt": 599000,
          |		"totalDebtIncInt": 599085,
          |		"planInterest": 85,
          |		"collections": {
          |			"initialCollection": {
          |				"dueDate": "2022-06-18",
          |				"amountDue": 1000
          |			},
          |			"regularCollections": [{
          |				"dueDate": "2022-08-08",
          |				"amountDue": 119817
          |			}, {
          |				"dueDate": "2022-09-08",
          |				"amountDue": 119817
          |			}, {
          |				"dueDate": "2022-10-08",
          |				"amountDue": 119817
          |			}, {
          |				"dueDate": "2022-11-08",
          |				"amountDue": 119817
          |			}, {
          |				"dueDate": "2022-12-08",
          |				"amountDue": 119817
          |			}]
          |		},
          |		"instalments": [{
          |			"instalmentNumber": 5,
          |			"dueDate": "2022-12-08",
          |			"instalmentInterestAccrued": 17,
          |			"instalmentBalance": 119800,
          |			"debtItemChargeId": "XW006559808862",
          |			"amountDue": 119800,
          |			"debtItemOriginalDueDate": "2021-07-08"
          |		}, {
          |			"instalmentNumber": 4,
          |			"dueDate": "2022-11-08",
          |			"instalmentInterestAccrued": 17,
          |			"instalmentBalance": 239600,
          |			"debtItemChargeId": "XW006559808862",
          |			"amountDue": 119800,
          |			"debtItemOriginalDueDate": "2021-07-08"
          |		}, {
          |			"instalmentNumber": 3,
          |			"dueDate": "2022-10-08",
          |			"instalmentInterestAccrued": 17,
          |			"instalmentBalance": 359400,
          |			"debtItemChargeId": "XW006559808862",
          |			"amountDue": 119800,
          |			"debtItemOriginalDueDate": "2021-07-08"
          |		}, {
          |			"instalmentNumber": 2,
          |			"dueDate": "2022-09-08",
          |			"instalmentInterestAccrued": 17,
          |			"instalmentBalance": 479200,
          |			"debtItemChargeId": "XW006559808862",
          |			"amountDue": 119800,
          |			"debtItemOriginalDueDate": "2021-07-08"
          |		}, {
          |			"instalmentNumber": 1,
          |			"dueDate": "2022-08-08",
          |			"instalmentInterestAccrued": 17,
          |			"instalmentBalance": 599000,
          |			"debtItemChargeId": "XW006559808862",
          |			"amountDue": 119800,
          |			"debtItemOriginalDueDate": "2021-07-08"
          |		}]
          |	}, {
          |		"numberOfInstalments": 6,
          |		"planDuration": 6,
          |		"totalDebt": 599000,
          |		"totalDebtIncInt": 599102,
          |		"planInterest": 102,
          |		"collections": {
          |			"initialCollection": {
          |				"dueDate": "2022-06-18",
          |				"amountDue": 1000
          |			},
          |			"regularCollections": [{
          |				"dueDate": "2022-08-08",
          |				"amountDue": 99850
          |			}, {
          |				"dueDate": "2022-09-08",
          |				"amountDue": 99850
          |			}, {
          |				"dueDate": "2022-10-08",
          |				"amountDue": 99850
          |			}, {
          |				"dueDate": "2022-11-08",
          |				"amountDue": 99850
          |			}, {
          |				"dueDate": "2022-12-08",
          |				"amountDue": 99850
          |			}, {
          |				"dueDate": "2023-01-08",
          |				"amountDue": 99850
          |			}]
          |		},
          |		"instalments": [{
          |			"instalmentNumber": 6,
          |			"dueDate": "2023-01-08",
          |			"instalmentInterestAccrued": 17,
          |			"instalmentBalance": 99835,
          |			"debtItemChargeId": "XW006559808862",
          |			"amountDue": 99833,
          |			"debtItemOriginalDueDate": "2021-07-08"
          |		}, {
          |			"instalmentNumber": 5,
          |			"dueDate": "2022-12-08",
          |			"instalmentInterestAccrued": 17,
          |			"instalmentBalance": 199668,
          |			"debtItemChargeId": "XW006559808862",
          |			"amountDue": 99833,
          |			"debtItemOriginalDueDate": "2021-07-08"
          |		}, {
          |			"instalmentNumber": 4,
          |			"dueDate": "2022-11-08",
          |			"instalmentInterestAccrued": 17,
          |			"instalmentBalance": 299501,
          |			"debtItemChargeId": "XW006559808862",
          |			"amountDue": 99833,
          |			"debtItemOriginalDueDate": "2021-07-08"
          |		}, {
          |			"instalmentNumber": 3,
          |			"dueDate": "2022-10-08",
          |			"instalmentInterestAccrued": 17,
          |			"instalmentBalance": 399334,
          |			"debtItemChargeId": "XW006559808862",
          |			"amountDue": 99833,
          |			"debtItemOriginalDueDate": "2021-07-08"
          |		}, {
          |			"instalmentNumber": 2,
          |			"dueDate": "2022-09-08",
          |			"instalmentInterestAccrued": 17,
          |			"instalmentBalance": 499167,
          |			"debtItemChargeId": "XW006559808862",
          |			"amountDue": 99833,
          |			"debtItemOriginalDueDate": "2021-07-08"
          |		}, {
          |			"instalmentNumber": 1,
          |			"dueDate": "2022-08-08",
          |			"instalmentInterestAccrued": 17,
          |			"instalmentBalance": 599000,
          |			"debtItemChargeId": "XW006559808862",
          |			"amountDue": 99833,
          |			"debtItemOriginalDueDate": "2021-07-08"
          |		}]
          |	}]
          |}
          |""".stripMargin
      ).as[JsObject]
  }
}
