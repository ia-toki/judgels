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
            "com.fasterxml.jackson.core" % "jackson-databind" % "2.9.7",
            "com.google.dagger" % "dagger" % "2.19",
            "io.dropwizard" % "dropwizard-jersey" % "1.3.7",

            "com.fasterxml.jackson.dataformat" % "jackson-dataformat-cbor" % "2.9.7",
            "com.palantir.conjure.java.runtime" % "conjure-java-jackson-serialization" % "4.8.0",
            "com.palantir.conjure.java.runtime" % "conjure-java-jaxrs-client" % "4.8.0",
            "com.palantir.conjure.java.runtime" % "conjure-java-jersey-server" % "4.8.0",

            "com.google.dagger" % "dagger-compiler" % "2.19",
            "org.immutables" % "value" % "2.7.3"
        )
    )

lazy val judgelsServiceApi = RootProject(file("../../judgels-commons/judgels-service-api"))
