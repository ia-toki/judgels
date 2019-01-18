import Versions._

lazy val judgelsServiceCore = (project in file("."))
    .dependsOn(judgelsServiceApi, judgelsServiceJaxrs)
    .aggregate(judgelsServiceApi, judgelsServiceJaxrs)
    .settings(
        name := "judgels-service-core",
        scalaVersion := sbtScalaVersion,
        resolvers ++= Seq(
          "Palantir" at "https://dl.bintray.com/palantir/releases"
        ),
        libraryDependencies ++= Seq(
            "com.fasterxml.jackson.core" % "jackson-databind" % jacksonVersion,
            "com.google.dagger" % "dagger" % daggerVersion,
            "io.dropwizard" % "dropwizard-jersey" % dropwizardVersion,

            "com.palantir.conjure.java.runtime" % "conjure-java-jackson-serialization" % conjureJavaRuntimeVersion,
            "com.palantir.conjure.java.runtime" % "conjure-java-jersey-server" % conjureJavaRuntimeVersion,

            "com.google.dagger" % "dagger-compiler" % daggerVersion,
            "org.immutables" % "value" % immutablesVersion
        )
    )

lazy val judgelsServiceApi = RootProject(file("../../judgels-commons/judgels-service-api"))
lazy val judgelsServiceJaxrs = RootProject(file("../../judgels-commons/judgels-service-jaxrs"))
