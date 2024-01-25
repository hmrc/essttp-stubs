import sbt._

object AppDependencies {

  val boostrapVersion = "8.4.0"

  val compile: Seq[ModuleID] = Seq(
    // format: OFF
    "uk.gov.hmrc"       %% "bootstrap-backend-play-30"   % boostrapVersion,
    "org.typelevel"     %% "cats-core"                   % "2.10.0",
    "com.beachape"      %% "enumeratum"                  % "1.7.3",
    "uk.gov.hmrc"       %% "essttp-backend-cor-journey"  % "1.127.0"
  // format: ON
  )

  val test: Seq[ModuleID] = Seq(
    // format: OFF
    "uk.gov.hmrc"            %% "bootstrap-test-play-30" % boostrapVersion,
    "org.scalatest"          %% "scalatest"              % "3.2.17",
    "org.scalatestplus.play" %% "scalatestplus-play"     % "7.0.1"
  // format: ON
  ).map(_ % Test)
}
