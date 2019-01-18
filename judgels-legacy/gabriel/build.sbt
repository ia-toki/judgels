import Versions._

import de.johoop.testngplugin.TestNGPlugin
import de.johoop.jacoco4sbt.JacocoPlugin.jacoco

lazy val gabriel = (project in file("."))
    .enablePlugins(JavaAppPackaging)
    .dependsOn(gabrielcommons, gabrielblackbox, api, judgelsServiceJaxrs, sealtielApi)
    .aggregate(gabrielcommons, gabrielblackbox, api, judgelsServiceJaxrs, sealtielApi)
    .settings(
        name := "gabriel",
        scalaVersion := sbtScalaVersion,
        autoScalaLibrary := false,
        crossPaths := false,
        mainClass in (Compile, run) := Some("org.iatoki.judgels.gabriel.Main"),
        libraryDependencies ++= Seq(
            "com.typesafe" % "config" % "1.2.1"
        ),
        dependencyOverrides ++= Set(
          "com.google.guava" % "guava" % guavaVersion,
          "com.fasterxml.jackson.core" % "jackson-core" % jacksonVersion,
          "com.fasterxml.jackson.core" % "jackson-databind" % jacksonVersion,
          "com.fasterxml.jackson.datatype" % "jackson-datatype-jdk8" % jacksonVersion,
          "com.fasterxml.jackson.module" % "jackson-module-afterburner" % jacksonVersion
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

lazy val gabrielcommons = RootProject(file("../gabriel-commons"))
lazy val gabrielblackbox = RootProject(file("../gabriel-blackbox"))
lazy val api = RootProject(file("../api"))

lazy val judgelsServiceJaxrs = RootProject(file("../../judgels-backends/judgels-commons/judgels-service-jaxrs"))
lazy val sealtielApi = RootProject(file("../../judgels-backends/sealtiel/sealtiel-api"))
