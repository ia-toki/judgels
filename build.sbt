import de.johoop.testngplugin.TestNGPlugin
import de.johoop.jacoco4sbt.JacocoPlugin.jacoco

lazy val gabrielcommons = (project in file("."))
    .settings(
        name := "gabrielcommons",
        version := IO.read(file("version.properties")).trim,
        scalaVersion := "2.11.1",
        autoScalaLibrary := false,
        crossPaths := false,
        resolvers += "IA TOKI Artifactory" at "http://artifactory.ia-toki.org/artifactory/repo",
        libraryDependencies ++= Seq(
            "org.iatoki.judgels.sealtiel" % "sealtiel-message" % "1.0.4",
            "org.apache.commons" % "commons-lang3" % "3.3.2",
            "com.google.code.gson" % "gson" % "2.3.1",
            "commons-io" % "commons-io" % "2.4",
            "com.google.guava" % "guava" % "r05",
            "org.slf4j" % "slf4j-api" % "1.7.10",
            "ch.qos.logback" % "logback-classic" % "1.1.1",
            "ch.qos.logback" % "logback-core" % "1.1.1"
        )
    )
    .settings(TestNGPlugin.testNGSettings: _*)
    .settings(
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
