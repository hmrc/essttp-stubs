import sbt._

object AppDependencies {

  val compile: Seq[ModuleID] = Seq(
    "uk.gov.hmrc"       %% "bootstrap-backend-play-28"   % "5.18.0",
    "org.typelevel"     %% "cats-core"                   % "2.7.0",
    "com.beachape"      %% "enumeratum"                  % "1.7.0",
    "uk.gov.hmrc.mongo" %% "hmrc-mongo-play-28"          % "0.68.0",
    "uk.gov.hmrc"       %% "essttp-backend-cor-journey"  % "1.49.0"
  )

  val test: Seq[ModuleID] = Seq(
    "uk.gov.hmrc"            %% "bootstrap-test-play-28" % "5.18.0" % Test,
    "com.vladsch.flexmark"   %  "flexmark-all"           % "0.36.8" % Test,
    "org.scalatest"          %% "scalatest"              % "3.2.9"  % Test,
    "org.pegdown"            %  "pegdown"                % "1.6.0"  % Test,
    "org.scalatestplus.play" %% "scalatestplus-play"     % "5.1.0"  % Test
  )
}
