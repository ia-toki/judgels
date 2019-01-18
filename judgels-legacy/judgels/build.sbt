import Versions._

lazy val judgels = (project in file("."))
    .disablePlugins(sbt.plugins.JUnitXmlReportPlugin)
    .dependsOn(sandalphon, jerahmeel, gabriel)
    .aggregate(sandalphon, jerahmeel, gabriel)
    .settings(javaUnidocSettings: _*)
    .settings(
        name := "judgels",
        scalaVersion := sbtScalaVersion
    )
    .settings(
        sources in (Compile, doc) <<= sources in (Compile, doc) map { _.filterNot(f => (f.getName endsWith ".scala") || (f.getName contains "Routes")) }
    )

lazy val sandalphon = RootProject(file("../sandalphon"))
lazy val jerahmeel = RootProject(file("../jerahmeel"))
lazy val gabriel = RootProject(file("../gabriel"))
