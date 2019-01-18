import Versions._

import de.johoop.testngplugin.TestNGPlugin
import de.johoop.jacoco4sbt.JacocoPlugin.jacoco

lazy val gabrielcommons = (project in file("."))
    .dependsOn(gabrielApi, commons)
    .aggregate(gabrielApi, commons)
    .settings(
        name := "gabrielcommons",
        scalaVersion := sbtScalaVersion,
        autoScalaLibrary := false,
        crossPaths := false,
        libraryDependencies ++= Seq(
            "org.slf4j" % "slf4j-api" % "1.7.10",
            "ch.qos.logback" % "logback-classic" % "1.1.1",
            "ch.qos.logback" % "logback-core" % "1.1.1"
        )
    )
    .settings(TestNGPlugin.testNGSettings: _*)
    .settings(
        aggregate in test := false,
        aggregate in jacoco.cover := false,
        TestNGPlugin.testNGSuites := Seq("src/test/resources/testng.xml")
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

lazy val gabrielApi = RootProject(file("../../judgels-backends/gabriel/gabriel-api"))
lazy val commons = RootProject(file("../commons"))
