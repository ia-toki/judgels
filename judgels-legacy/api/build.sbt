import Versions._

val workaround = {
  sys.props += "packaging.type" -> "jar"
  ()
}

lazy val api = (project in file("."))
    .dependsOn(sealtielApi)
    .aggregate(sealtielApi)
    .settings(
        name := "api",
        scalaVersion := sbtScalaVersion,
        libraryDependencies ++= Seq(
            "com.puppycrawl.tools" % "checkstyle" % "6.8.1",
            "com.google.code.gson" % "gson" % "2.3.1",
            "com.google.guava" % "guava" % guavaVersion,
            "commons-io" % "commons-io" % "2.4",
            "org.apache.httpcomponents" % "httpclient" % "4.5",
            "org.apache.commons" % "commons-lang3" % apacheCommonsLang3Version
        )
    )

lazy val sealtielApi = RootProject(file("../../judgels-backends/sealtiel/sealtiel-api"))
