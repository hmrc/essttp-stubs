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

package uk.gov.hmrc.essttpstubs.testutil

import akka.util.Timeout
import com.google.inject.AbstractModule
import com.typesafe.config.ConfigFactory
import org.scalatest.freespec.AnyFreeSpec
import org.scalatest.matchers.should.Matchers
import org.scalatest.time.{ Millis, Seconds, Span }
import org.scalatest.BeforeAndAfterEach
import org.scalatestplus.play.guice.GuiceOneServerPerSuite
import play.api.{ Application, Configuration, Mode }
import play.api.inject.Injector
import play.api.inject.guice.{ GuiceApplicationBuilder, GuiceableModule }
import play.api.mvc.Result
import play.api.test.{ DefaultTestServerFactory, RunningServer }
import play.core.server.ServerConfig
import uk.gov.hmrc.essttpstubs.repo.EligibilityRepo
import uk.gov.hmrc.essttpstubs.testutil.connector.TestEligibilityConnector
import uk.gov.hmrc.http.HeaderCarrier

import scala.concurrent.{ Await, ExecutionContext, Future }

/**
 * This is common spec for every test case which brings all of useful routines we want to use in our scenarios.
 */

trait ItSpec
  extends AnyFreeSpec
  with RichMatchers
  with BeforeAndAfterEach
  with GuiceOneServerPerSuite
  with Matchers {
  self =>

  implicit val ec: ExecutionContext = scala.concurrent.ExecutionContext.Implicits.global
  implicit val emptyHC: HeaderCarrier = HeaderCarrier()

  override implicit val patienceConfig: PatienceConfig = PatienceConfig(
    timeout = scaled(Span(3, Seconds)),
    interval = scaled(Span(300, Millis)))

  lazy val injector: Injector = fakeApplication().injector
  lazy val testEligibilityConnector: TestEligibilityConnector = injector.instanceOf[TestEligibilityConnector]
  lazy val eligibilityRepo: EligibilityRepo = injector.instanceOf[EligibilityRepo]

  lazy val additionalConfig: Configuration = Configuration()

  override def fakeApplication(): Application = new GuiceApplicationBuilder()
    .overrides(GuiceableModule.fromGuiceModules(Seq(new AbstractModule {
      override def configure(): Unit = ()
    })))
    .configure(
      Configuration(
        ConfigFactory.parseString(
          """
            |  mongodb.uri = "mongodb://localhost:27017/essttp-stubs-eligibility",
            |  metrics.enabled = false
            |""".stripMargin)).withFallback(additionalConfig)).build()

  override def beforeEach(): Unit = {
    super.beforeEach()
    eligibilityRepo.removeAll().futureValue
    ()
  }

  def status(of: Result): Int = of.header.status

  def status(of: Future[Result])(implicit timeout: Timeout): Int = Await.result(of, timeout.duration).header.status

  override implicit protected lazy val runningServer: RunningServer =
    TestServerFactory.start(app)

  object TestServerFactory extends DefaultTestServerFactory {
    override protected def serverConfig(app: Application): ServerConfig = {
      val sc = ServerConfig(port = Some(ItSpec.testServerPort), sslPort = Some(0), mode = Mode.Test, rootDir = app.path)
      sc.copy(configuration = sc.configuration.withFallback(overrideServerConfiguration(app)))
    }
  }
}

object ItSpec {

  val testServerPort = 19001

}
