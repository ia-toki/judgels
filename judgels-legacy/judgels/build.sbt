lazy val judgels = (project in file("."))
    .disablePlugins(sbt.plugins.JUnitXmlReportPlugin)
    .dependsOn(sandalphon)
    .aggregate(sandalphon)
    .settings(javaUnidocSettings: _*)
    .settings(
        name := "judgels",
        version := IO.read(file("version.properties")).trim,
        scalaVersion := "2.11.7"
    )
    .settings(
        sources in (Compile, doc) <<= sources in (Compile, doc) map { _.filterNot(f => (f.getName endsWith ".scala") || (f.getName contains "Routes")) }
    )

lazy val sandalphon = RootProject(file("../sandalphon"))
