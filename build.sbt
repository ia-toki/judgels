import de.johoop.testngplugin.TestNGPlugin
import de.johoop.jacoco4sbt.JacocoPlugin.jacoco

lazy val gabrielcommons = (project in file("."))
    .dependsOn(judgelscommons)
    .aggregate(judgelscommons)
    .settings(
        name := "gabrielcommons",
        version := IO.read(file("version.properties")).trim,
        scalaVersion := "2.11.7",
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

lazy val judgelscommons = RootProject(file("../judgels-commons"))
