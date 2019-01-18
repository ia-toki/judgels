import Versions._

lazy val jophielApi = (project in file("."))
  .dependsOn(judgelsServiceApi, judgelsPersistenceApi)
  .aggregate(judgelsServiceApi, judgelsPersistenceApi)
  .settings(
    name := "sealtiel-api",
    scalaVersion := sbtScalaVersion,
    libraryDependencies ++= Seq(
      "com.fasterxml.jackson.core" % "jackson-databind" % jacksonVersion,
      "org.immutables" % "value" % immutablesVersion
    )
  )

lazy val judgelsServiceApi = RootProject(file("../../judgels-commons/judgels-service-api"))
lazy val judgelsPersistenceApi = RootProject(file("../../judgels-commons/judgels-persistence-api"))
