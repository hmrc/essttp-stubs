import sbt._

object AppDependencies {

  val boostrapVersion = "7.11.0"

  val compile: Seq[ModuleID] = Seq(
    "uk.gov.hmrc"       %% "bootstrap-backend-play-28"   % boostrapVersion,
    "org.typelevel"     %% "cats-core"                   % "2.8.0",
    "com.beachape"      %% "enumeratum"                  % "1.7.0",
    "uk.gov.hmrc.mongo" %% "hmrc-mongo-play-28"          % "0.73.0",
    "uk.gov.hmrc"       %% "essttp-backend-cor-journey"  % "1.77.0"
  )

  val test: Seq[ModuleID] = Seq(
    "uk.gov.hmrc"            %% "bootstrap-test-play-28" % boostrapVersion,
    "com.vladsch.flexmark"   %  "flexmark-all"           % "0.62.2",
    "org.scalatest"          %% "scalatest"              % "3.2.14" ,
    "org.pegdown"            %  "pegdown"                % "1.6.0" ,
    "org.scalatestplus.play" %% "scalatestplus-play"     % "5.1.0"
  ).map(_ % Test)
}
