import Versions._

lazy val judgelsServiceCore = (project in file("."))
    .dependsOn(judgelsServiceApi)
    .aggregate(judgelsServiceApi)
    .settings(
        name := "judgels-service-core",
        scalaVersion := "2.11.7",
        resolvers ++= Seq(
          "Palantir" at "https://dl.bintray.com/palantir/releases"
        ),
        libraryDependencies ++= Seq(
            "com.fasterxml.jackson.core" % "jackson-databind" % jacksonVersion,
            "com.google.dagger" % "dagger" % daggerVersion,
            "io.dropwizard" % "dropwizard-jersey" % dropwizardVersion,

            "com.fasterxml.jackson.dataformat" % "jackson-dataformat-cbor" % jacksonVersion,
            "com.palantir.conjure.java.runtime" % "conjure-java-jackson-serialization" % conjureJavaRuntimeVersion,
            "com.palantir.conjure.java.runtime" % "conjure-java-jaxrs-client" % conjureJavaRuntimeVersion,
            "com.palantir.conjure.java.runtime" % "conjure-java-jersey-server" % conjureJavaRuntimeVersion,

            "com.google.dagger" % "dagger-compiler" % daggerVersion,
            "org.immutables" % "value" % immutablesVersion
        )
    )

lazy val judgelsServiceApi = RootProject(file("../../judgels-commons/judgels-service-api"))
