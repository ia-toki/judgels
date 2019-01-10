import de.johoop.testngplugin.TestNGPlugin
import de.johoop.jacoco4sbt.JacocoPlugin.jacoco

lazy val gabrielblackbox = (project in file("."))
    .dependsOn(gabrielcommons)
    .aggregate(gabrielcommons)
    .settings(
        name := "gabriel-blackbox",
        version := IO.read(file("version.properties")).trim,
        scalaVersion := "2.11.7",
        autoScalaLibrary := false,
        crossPaths := false
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

lazy val gabrielcommons = RootProject(file("../gabriel-commons"))
