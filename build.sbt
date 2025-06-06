
val appName = "essttp-stubs"

lazy val scalaCompilerOptions = Seq(
  "-Xfatal-warnings",
  "-Wvalue-discard",
  "-deprecation",
  "-feature",
  "-unchecked",
  "-language:implicitConversions",
  "-language:strictEquality",
  // required in place of silencer plugin
  "-Wconf:msg=unused-imports&src=html/.*:s",
  "-Wconf:src=routes/.*:s"
)

lazy val microservice = Project(appName, file("."))
  .enablePlugins(play.sbt.PlayScala, SbtDistributablesPlugin)
  .disablePlugins(sbt.plugins.JUnitXmlReportPlugin)
  .settings(
    majorVersion                     := 1,
    scalaVersion                     := "3.5.1",
    scalacOptions ++= scalaCompilerOptions,
    libraryDependencies              ++= AppDependencies.compile ++ AppDependencies.test,
    scalafmtOnCompile := true
  )
  .settings(WartRemoverSettings.wartRemoverSettings)
  .settings(SbtUpdatesSettings.sbtUpdatesSettings)
  .settings(PlayKeys.playDefaultPort := 9218)
  .settings(routesImport ++= Seq())
  .settings(scalacOptions -= "utf8")
