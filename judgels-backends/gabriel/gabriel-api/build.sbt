import Versions._

lazy val gabrielApi = (project in file("."))
  .settings(
    name := "gabriel-api",
    scalaVersion := "2.11.7",
    libraryDependencies ++= Seq(
      "com.fasterxml.jackson.core" % "jackson-databind" % jacksonVersion,
      "org.immutables" % "value" % immutablesVersion
    )
  )
