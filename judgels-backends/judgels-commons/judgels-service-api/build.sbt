lazy val judgelsServiceApi = (project in file("."))
  .settings(
    name := "judgels-service-api",
    scalaVersion := "2.11.7",
    resolvers ++= Seq(
      "Palantir" at "https://dl.bintray.com/palantir/releases"
    ),
    libraryDependencies ++= Seq(
      "com.fasterxml.jackson.core" % "jackson-databind" % "2.9.7",
      "javax.ws.rs" % "javax.ws.rs-api" % "2.1",
      "com.palantir.conjure.java.api" % "errors" % "2.0.0",
      "com.palantir.conjure.java.api" % "service-config" % "2.0.0",
      "org.immutables" % "value" % "2.7.3"
    )
  )
