import de.johoop.testngplugin.TestNGPlugin
import de.johoop.jacoco4sbt.JacocoPlugin.jacoco
import sbtbuildinfo.Plugin._

lazy val sandalphon = (project in file("."))
    .enablePlugins(PlayJava, SbtWeb)
    .disablePlugins(sbt.plugins.JUnitXmlReportPlugin)
    .dependsOn(sandalphoncommons, jophielcommons, sandalphonblackboxadapters)
    .aggregate(sandalphoncommons, jophielcommons, sandalphonblackboxadapters)
    .settings(
        name := "sandalphon",
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
        buildInfoPackage := "org.iatoki.judgels.sandalphon"
    )
    .settings(
      dependencyOverrides ++= Set(
        "com.google.guava" % "guava" % "20.0",
        "com.fasterxml.jackson.core" % "jackson-core" % "2.9.1",
        "com.fasterxml.jackson.core" % "jackson-databind" % "2.9.1",
        "com.fasterxml.jackson.datatype" % "jackson-datatype-jdk8" % "2.9.1",
        "com.fasterxml.jackson.module" % "jackson-module-afterburner" % "2.9.1"
      )
    )

lazy val sandalphoncommons = RootProject(file("../sandalphon-commons"))
lazy val jophielcommons = RootProject(file("../jophiel-commons"))
lazy val sandalphonblackboxadapters = RootProject(file("../sandalphon-blackbox-adapters"))
