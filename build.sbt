import uk.gov.hmrc.DefaultBuildSettings.integrationTestSettings
import uk.gov.hmrc.sbtdistributables.SbtDistributablesPlugin.publishingSettings

val appName = "essttp-stubs"

lazy val scalaCompilerOptions = Seq(
  "-Xfatal-warnings",
  "-Xlint:-missing-interpolator,_",
  "-Yno-adapted-args",
  "-Ywarn-value-discard",
  "-Ywarn-dead-code",
  "-deprecation",
  "-feature",
  "-unchecked",
  "-language:implicitConversions",
  "-Ypartial-unification", //required by cats
  // required in place of silencer plugin
  "-Wconf:cat=unused-imports&src=html/.*:s",
  "-Wconf:src=routes/.*:s"
)

lazy val microservice = Project(appName, file("."))
  .enablePlugins(play.sbt.PlayScala, SbtDistributablesPlugin)
  .disablePlugins(sbt.plugins.JUnitXmlReportPlugin)
  .settings(
    majorVersion                     := 0,
    scalaVersion                     := "2.12.15",
    scalacOptions ++= scalaCompilerOptions,
    libraryDependencies              ++= AppDependencies.compile ++ AppDependencies.test
  )
  .settings(WartRemoverSettings.wartRemoverSettings)
  .settings(ScalariformSettings.scalariformSettings)
  .settings(SbtUpdatesSettings.sbtUpdatesSettings)
  .settings(publishingSettings: _*)
  .configs(IntegrationTest)
  .settings(integrationTestSettings(): _*)
  .settings(resolvers += Resolver.jcenterRepo)
  .settings(PlayKeys.playDefaultPort := 9218)
  .settings(routesImport ++= Seq())
  .settings(scalacOptions -= "utf8")
