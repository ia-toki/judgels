import de.johoop.testngplugin.TestNGPlugin
import de.johoop.jacoco4sbt.JacocoPlugin.jacoco
import sbtbuildinfo.Plugin._

lazy val jerahmeel = (project in file("."))
    .enablePlugins(PlayJava, SbtWeb)
    .disablePlugins(sbt.plugins.JUnitXmlReportPlugin)
    .dependsOn(sandalphoncommons, jophielcommons, sandalphonblackboxadapters)
    .aggregate(sandalphoncommons, jophielcommons, sandalphonblackboxadapters)
    .settings(
        name := "jerahmeel",
        version := IO.read(file("version.properties")).trim,
        scalaVersion := "2.11.7",
        routesGenerator := InjectedRoutesGenerator,
        PlayKeys.externalizeResources := false
    )
    .settings(TestNGPlugin.testNGSettings: _*)
    .settings(
        aggregate in test := false,
        aggregate in dist := false,
        aggregate in jacoco.cover := false,
        TestNGPlugin.testNGSuites := Seq("test/resources/testng.xml")
    )
    .settings(jacoco.settings: _*)
    .settings(
        parallelExecution in jacoco.Config := false
    )
    .settings(
        LessKeys.compress := true,
        LessKeys.optimization := 3,
        LessKeys.verbose := true
    )
    .settings(
        publishArtifact in (Compile, packageDoc) := false,
        publishArtifact in packageDoc := false,
        sources in (Compile,doc) := Seq.empty
    )
    .settings(buildInfoSettings: _*)
    .settings(
        sourceGenerators in Compile <+= buildInfo,
        buildInfoKeys := Seq[BuildInfoKey](name, version),
        buildInfoPackage := "org.iatoki.judgels.jerahmeel"
    )
    .settings(
        dependencyOverrides ++= Set(
            "com.google.guava" % "guava" % "27.0.1-jre",
            "com.fasterxml.jackson.core" % "jackson-core" % "2.9.7",
            "com.fasterxml.jackson.core" % "jackson-databind" % "2.9.7",
            "com.fasterxml.jackson.datatype" % "jackson-datatype-jdk8" % "2.9.7",
            "com.fasterxml.jackson.module" % "jackson-module-afterburner" % "2.9.7"
        )
    )

lazy val playcommons = RootProject(file("../play-commons"))
lazy val sandalphoncommons = RootProject(file("../sandalphon-commons"))
lazy val jophielcommons = RootProject(file("../jophiel-commons"))
lazy val sandalphonblackboxadapters = RootProject(file("../sandalphon-blackbox-adapters"))

lazy val judgelsServiceCore = RootProject(file("../../judgels-backends/judgels-commons/judgels-service-core"))
