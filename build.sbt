import de.johoop.testngplugin.TestNGPlugin
import de.johoop.jacoco4sbt.JacocoPlugin.jacoco

name := """gabrielcommons"""

version := "0.1.0"

autoScalaLibrary := false

crossPaths := false

scalaVersion := "2.11.1"

libraryDependencies ++= Seq(
  "commons-io" % "commons-io" % "2.4",
  "org.apache.commons" % "commons-lang3" % "3.3.2",
  "com.google.guava" % "guava" % "18.0",
  "com.google.code.gson" % "gson" % "2.3.1",
  "org.slf4j" % "slf4j-api" % "1.7.10",
  "ch.qos.logback" % "logback-classic" % "1.1.1",
  "ch.qos.logback" % "logback-core" % "1.1.1"
)

TestNGPlugin.testNGSettings

TestNGPlugin.testNGSuites := Seq("testng.xml")

TestNGPlugin.testNGOutputDirectory := "target/testng"

jacoco.settings

parallelExecution in jacoco.Config := false

