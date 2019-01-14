import Versions._

lazy val judgelsPersistenceApi = (project in file("."))
  .settings(
    name := "judgels-persistence-api",
    scalaVersion := "2.11.7",
    libraryDependencies ++= Seq(
      "com.fasterxml.jackson.core" % "jackson-databind" % jacksonVersion,
      "org.immutables" % "value" % immutablesVersion
    )
  )
