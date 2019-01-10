import de.johoop.testngplugin.TestNGPlugin
import de.johoop.jacoco4sbt.JacocoPlugin.jacoco

lazy val sandalphonblackboxadapters = (project in file("."))
    .enablePlugins(PlayJava)
    .disablePlugins(plugins.JUnitXmlReportPlugin)
    .dependsOn(sandalphoncommons, gabrielblackbox)
    .aggregate(sandalphoncommons, gabrielblackbox)
    .settings(
        name := "sandalphonblackboxadapters",
        version := IO.read(file("version.properties")).trim,
        scalaVersion := "2.11.7",
        routesGenerator := InjectedRoutesGenerator
    )
    .settings(TestNGPlugin.testNGSettings: _*)
    .settings(
        aggregate in test := false,
        aggregate in jacoco.cover := false,
        TestNGPlugin.testNGSuites := Seq("test/resources/testng.xml")
    )
    .settings(jacoco.settings: _*)
    .settings(
        parallelExecution in jacoco.Config := false
    )
    .settings(
        publishArtifact in (Compile, packageDoc) := false,
        publishArtifact in packageDoc := false,
        sources in (Compile,doc) := Seq.empty
    )

lazy val sandalphoncommons = RootProject(file("../sandalphon-commons"))
lazy val gabrielblackbox = RootProject(file("../gabriel-blackbox"))
