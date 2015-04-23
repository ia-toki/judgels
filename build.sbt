import de.johoop.testngplugin.TestNGPlugin
import de.johoop.jacoco4sbt.JacocoPlugin.jacoco

lazy val gabriel = (project in file("."))
    .dependsOn(gabrielcommons)
    .aggregate(gabrielcommons)
    .settings(
        name := "gabriel",
        version := IO.read(file("version.properties")).trim,
        scalaVersion := "2.11.1",
        autoScalaLibrary := false,
        crossPaths := false,
        mainClass in (Compile, run) := Some("org.iatoki.judgels.gabriel.Main"),
        libraryDependencies ++= Seq(
            "org.apache.httpcomponents" % "httpclient" % "4.4"
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

lazy val gabrielcommons = RootProject(file("../judgels-gabriel-commons"))

