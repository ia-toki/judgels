val checkstyle = taskKey[Unit]("Execute checkstyle")

checkstyle := {
    val v = ("../judgels/scripts/execute-checkstyle.sh" !)
    if (v != 0) {
        sys.error("Failed")
    }
}

val workaround = {
  sys.props += "packaging.type" -> "jar"
  ()
}

lazy val api = (project in file("."))
    .settings(
        name := "api",
        version := IO.read(file("version.properties")).trim,
        scalaVersion := "2.11.7",
        resolvers ++= Seq(
            "Judgels" at "https://dl.bintray.com/judgels/releases",
            "Palantir" at "http://palantir.bintray.com/releases"
        ),
        libraryDependencies ++= Seq(
            "com.palantir.remoting3" % "jaxrs-clients" % "3.15.0",
            "com.puppycrawl.tools" % "checkstyle" % "6.8.1",
            "com.google.code.gson" % "gson" % "2.3.1",
            "com.google.guava" % "guava" % "20.0",
            "commons-io" % "commons-io" % "2.4",
            "org.apache.httpcomponents" % "httpclient" % "4.5",
            "org.apache.commons" % "commons-lang3" % "3.3.2",
            "org.iatoki.judgels" % "sealtiel-api" % "0.5.1"
        )
    )
