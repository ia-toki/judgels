import Versions._

lazy val judgelsServiceApi = (project in file("."))
  .settings(
    name := "judgels-service-api",
    scalaVersion := sbtScalaVersion,
    resolvers ++= Seq(
      "Palantir" at "https://dl.bintray.com/palantir/releases"
    ),
    libraryDependencies ++= Seq(
      "com.fasterxml.jackson.core" % "jackson-databind" % jacksonVersion,
      "javax.ws.rs" % "javax.ws.rs-api" % jaxRsApiVersion,
      "com.palantir.conjure.java.api" % "errors" % conjureJavaRuntimeApiVersion,
      "com.palantir.conjure.java.api" % "service-config" % conjureJavaRuntimeApiVersion,
      "org.immutables" % "value" % immutablesVersion
    )
  )
