import Versions._

lazy val judgelsPersistenceApi = (project in file("."))
  .settings(
    name := "judgels-persistence-api",
    scalaVersion := sbtScalaVersion,
    libraryDependencies ++= Seq(
      "com.fasterxml.jackson.core" % "jackson-databind" % jacksonVersion,
      "org.immutables" % "value" % immutablesVersion
    )
  )
