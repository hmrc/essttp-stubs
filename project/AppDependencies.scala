import sbt.*

object AppDependencies {

  val boostrapVersion = "9.13.0"

  val compile: Seq[ModuleID] = Seq(
    // format: OFF
    "uk.gov.hmrc"       %% "bootstrap-backend-play-30"   % boostrapVersion,
    "org.typelevel"     %% "cats-core"                   % "2.13.0",
    "com.beachape"      %% "enumeratum"                  % "1.9.0",
    "uk.gov.hmrc"       %% "essttp-backend-cor-journey"  % "2.8.0",
  // format: ON
  )

  val test: Seq[ModuleID] = Seq(
    // format: OFF
    "uk.gov.hmrc"            %% "bootstrap-test-play-30" % boostrapVersion,
    "org.scalatest"          %% "scalatest"              % "3.2.19",
    "org.scalatestplus.play" %% "scalatestplus-play"     % "7.0.1"
  // format: ON
  ).map(_ % Test)
}
