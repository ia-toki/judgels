import de.johoop.testngplugin.TestNGPlugin
import de.johoop.jacoco4sbt.JacocoPlugin.jacoco

lazy val sandalphoncommons = (project in file("."))
    .enablePlugins(PlayJava, SbtWeb)
    .disablePlugins(plugins.JUnitXmlReportPlugin)
    .dependsOn(playcommons, gabrielcommons, api, judgelsServiceCore)
    .aggregate(playcommons, gabrielcommons, api, judgelsServiceCore)
    .settings(
        name := "sandalphoncommons",
        version := IO.read(file("version.properties")).trim,
        scalaVersion := "2.11.7",
        libraryDependencies ++= Seq(
            "com.ibm.icu" % "icu4j" % "55.1",
            "org.webjars" % "prettify" % "4-Mar-2013",
            "com.warrenstrange" % "googleauth" % "0.4.3"
        ),
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

lazy val playcommons = RootProject(file("../play-commons"))
lazy val gabrielcommons = RootProject(file("../gabriel-commons"))
lazy val api = RootProject(file("../api"))

lazy val judgelsServiceCore = RootProject(file("../../judgels-backends/judgels-commons/judgels-service-core"))
