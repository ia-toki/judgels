import de.johoop.testngplugin.TestNGPlugin
import de.johoop.jacoco4sbt.JacocoPlugin.jacoco

lazy val gabriel = (project in file("."))
    .dependsOn(gabrielcommons, sealtielcommons, gabrielblackbox)
    .aggregate(gabrielcommons, sealtielcommons, gabrielblackbox)
    .settings(
        name := "gabriel",
        version := IO.read(file("version.properties")).trim,
        scalaVersion := "2.11.7",
        autoScalaLibrary := false,
        crossPaths := false,
        mainClass in (Compile, run) := Some("org.iatoki.judgels.gabriel.Main"),
        libraryDependencies ++= Seq(
            "com.typesafe" % "config" % "1.2.1"
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
lazy val sealtielcommons = RootProject(file("../sealtiel-commons"))
lazy val gabrielblackbox = RootProject(file("../gabriel-blackbox"))
