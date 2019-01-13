import Versions._

lazy val sealtielApi = (project in file("."))
    .dependsOn(judgelsServiceApi)
    .aggregate(judgelsServiceApi)
    .settings(
        name := "sealtiel-api",
        scalaVersion := "2.11.7",
        libraryDependencies ++= Seq(
            "com.fasterxml.jackson.core" % "jackson-databind" % jacksonVersion,
            "org.immutables" % "value" % immutablesVersion
        )
  )

lazy val judgelsServiceApi = RootProject(file("../../judgels-commons/judgels-service-api"))
