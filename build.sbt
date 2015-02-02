import de.johoop.testngplugin.TestNGPlugin
import de.johoop.jacoco4sbt.JacocoPlugin.jacoco

name := """gabriel"""

version := "1.0-SNAPSHOT"

lazy val gabriel = (project in file(".")).dependsOn(gabrielcommons).aggregate(gabrielcommons)

lazy val gabrielcommons = RootProject(file("../gabriel-commons"))

mainClass in (Compile, run) := Some("org.iatoki.judgels.gabriel.Main")

autoScalaLibrary := false

crossPaths := false

scalaVersion := "2.11.1"

resolvers += "IA TOKI Artifactory" at "http://artifactory.ia-toki.org/artifactory/repo"

libraryDependencies ++= Seq(
  "commons-io" % "commons-io" % "2.4",
  "com.google.guava" % "guava" % "18.0",
  "com.google.code.gson" % "gson" % "2.3.1",
  "org.iatoki.judgels.sealtiel" % "sealtielMessage" % "1.0.0"
)

TestNGPlugin.testNGSettings

TestNGPlugin.testNGSuites := Seq("testng.xml")

TestNGPlugin.testNGOutputDirectory := "target/testng"

jacoco.settings

parallelExecution in jacoco.Config := false

