import Versions._

import de.johoop.testngplugin.TestNGPlugin
import de.johoop.jacoco4sbt.JacocoPlugin.jacoco

lazy val commons = (project in file("."))
    .settings(
        name := "commons",
        scalaVersion := sbtScalaVersion,
        libraryDependencies ++= Seq(
            "com.google.code.gson" % "gson" % "2.3.1",
            "com.google.guava" % "guava" % guavaVersion,
            "com.typesafe" % "config" % "1.3.0",
            "commons-io" % "commons-io" % "2.4",
            "org.apache.httpcomponents" % "httpclient" % "4.5",
            "org.apache.commons" % "commons-lang3" % apacheCommonsLang3Version,
            "org.eclipse.jgit" % "org.eclipse.jgit" % "3.7.0.201502260915-r",
            "com.amazonaws" % "aws-java-sdk" % awsJavaSdkS3Version exclude("joda-time", "joda-time")
        ),
        resolvers += Resolver.sbtPluginRepo("releases")
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


fork in run := true
