import Versions._

lazy val sandalphonApi = (project in file("."))
  .dependsOn(jophielApi, gabrielApi)
  .aggregate(jophielApi, gabrielApi)
  .settings(
    name := "sandalphon-api",
    scalaVersion := "2.11.7",
    libraryDependencies ++= Seq(
      "com.fasterxml.jackson.core" % "jackson-databind" % jacksonVersion,
      "org.immutables" % "value" % immutablesVersion
    )
  )

lazy val jophielApi = RootProject(file("../../jophiel/jophiel-api"))
lazy val gabrielApi = RootProject(file("../../gabriel/gabriel-api"))
