import sbt._

object AppDependencies {

  val boostrapVersion = "7.22.0"

  val compile: Seq[ModuleID] = Seq(
    // format: OFF
    "uk.gov.hmrc"       %% "bootstrap-backend-play-28"   % boostrapVersion,
    "org.typelevel"     %% "cats-core"                   % "2.10.0",
    "com.beachape"      %% "enumeratum"                  % "1.7.3",
    "uk.gov.hmrc"       %% "essttp-backend-cor-journey"  % "1.119.0"
  // format: ON
  )

  val test: Seq[ModuleID] = Seq(
    // format: OFF
    "uk.gov.hmrc"            %% "bootstrap-test-play-28" % boostrapVersion,
    "com.vladsch.flexmark"   %  "flexmark-all"           % "0.64.6",
    "org.scalatest"          %% "scalatest"              % "3.2.17" ,
    "org.pegdown"            %  "pegdown"                % "1.6.0" ,
    "org.scalatestplus.play" %% "scalatestplus-play"     % "5.1.0"
  // format: ON
  ).map(_ % Test)
}
