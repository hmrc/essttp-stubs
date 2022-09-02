import com.typesafe.sbt.SbtScalariform.ScalariformKeys
import scalariform.formatter.preferences.{AlignArguments, AlignParameters, AlignSingleLineCaseStatements, AllowParamGroupsOnNewlines, CompactControlReadability, CompactStringConcatenation, DanglingCloseParenthesis, DoubleIndentConstructorArguments, DoubleIndentMethodDeclaration, FirstArgumentOnNewline, FirstParameterOnNewline, Force, FormatXml, IndentLocalDefs, IndentPackageBlocks, IndentSpaces, IndentWithTabs, MultilineScaladocCommentsStartOnFirstLine, NewlineAtEndOfFile, PlaceScaladocAsterisksBeneathSecondAsterisk, PreserveSpaceBeforeArguments, RewriteArrowSymbols, SpaceBeforeColon, SpaceBeforeContextColon, SpaceInsideBrackets, SpaceInsideParentheses, SpacesAroundMultiImports, SpacesWithinPatternBinders}
import scoverage.ScoverageKeys
import uk.gov.hmrc.DefaultBuildSettings.integrationTestSettings
import uk.gov.hmrc.sbtdistributables.SbtDistributablesPlugin.publishingSettings
import wartremover.Wart

val appName = "essttp-stubs"

lazy val scalariformSettings =
// description of options found here -> https://github.com/scala-ide/scalariform
  ScalariformKeys.preferences := ScalariformKeys.preferences.value
    .setPreference(AlignArguments, true)
    .setPreference(AlignParameters, true)
    .setPreference(AlignSingleLineCaseStatements, true)
    .setPreference(AllowParamGroupsOnNewlines, true)
    .setPreference(CompactControlReadability, false)
    .setPreference(CompactStringConcatenation, false)
    .setPreference(DanglingCloseParenthesis, Force)
    .setPreference(DoubleIndentConstructorArguments, true)
    .setPreference(DoubleIndentMethodDeclaration, true)
    .setPreference(FirstArgumentOnNewline, Force)
    .setPreference(FirstParameterOnNewline, Force)
    .setPreference(FormatXml, true)
    .setPreference(IndentLocalDefs, true)
    .setPreference(IndentPackageBlocks, true)
    .setPreference(IndentSpaces, 2)
    .setPreference(IndentWithTabs, false)
    .setPreference(MultilineScaladocCommentsStartOnFirstLine, false)
    .setPreference(NewlineAtEndOfFile, true)
    .setPreference(PlaceScaladocAsterisksBeneathSecondAsterisk, false)
    .setPreference(PreserveSpaceBeforeArguments, true)
    .setPreference(RewriteArrowSymbols, false)
    .setPreference(SpaceBeforeColon, false)
    .setPreference(SpaceBeforeContextColon, false)
    .setPreference(SpaceInsideBrackets, false)
    .setPreference(SpaceInsideParentheses, false)
    .setPreference(SpacesAroundMultiImports, false)
    .setPreference(SpacesWithinPatternBinders, true)

lazy val wartRemoverSettings =
  Seq(
    (Compile / compile / wartremoverErrors) ++= Warts.allBut(
      Wart.DefaultArguments,
      Wart.ImplicitConversion,
      Wart.ImplicitParameter,
      Wart.Nothing,
      Wart.Overloading,
      Wart.Throw,
      Wart.ToString
    ),
    Test / compile / wartremoverErrors --= Seq(
      Wart.Any,
      Wart.Equals,
      Wart.GlobalExecutionContext,
      Wart.Null,
      Wart.NonUnitStatements,
      Wart.PublicInference
    ),
    wartremoverExcluded ++= (
      (baseDirectory.value ** "*.sc").get ++
        (Compile / routes).value
      )
  )

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
  .settings(wartRemoverSettings)
  .settings(scalariformSettings)
  .settings(publishingSettings: _*)
  .configs(IntegrationTest)
  .settings(integrationTestSettings(): _*)
  .settings(resolvers += Resolver.jcenterRepo)
  .settings(PlayKeys.playDefaultPort := 9218)
  .settings(routesImport ++= Seq())
  .settings(scalacOptions -= "utf8")
