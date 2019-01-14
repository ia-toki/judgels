import Versions._

lazy val judgelsServiceJaxrs = (project in file("."))
  .settings(
    name := "judgels-service-jaxrs",
    scalaVersion := "2.11.7",
    resolvers ++= Seq(
      "Palantir" at "https://dl.bintray.com/palantir/releases"
    ),
    libraryDependencies ++= Seq(
      "com.fasterxml.jackson.core" % "jackson-databind" % jacksonVersion,
      "com.palantir.conjure.java.runtime" % "conjure-java-jackson-serialization" % conjureJavaRuntimeVersion,
      "com.palantir.conjure.java.runtime" % "conjure-java-jaxrs-client" % conjureJavaRuntimeVersion
    )
  )
