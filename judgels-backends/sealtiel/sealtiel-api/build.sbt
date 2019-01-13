lazy val sealtielApi = (project in file("."))
    .dependsOn(judgelsServiceApi)
    .aggregate(judgelsServiceApi)
    .settings(
        name := "sealtiel-api",
        scalaVersion := "2.11.7",
        libraryDependencies ++= Seq(
            "com.fasterxml.jackson.core" % "jackson-databind" % "2.9.7",
            "org.immutables" % "value" % "2.7.3"
        )
  )

lazy val judgelsServiceApi = RootProject(file("../../judgels-commons/judgels-service-api"))
