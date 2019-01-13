lazy val gabrielApi = (project in file("."))
  .settings(
    name := "gabriel-api",
    scalaVersion := "2.11.7",
    libraryDependencies ++= Seq(
      "com.fasterxml.jackson.core" % "jackson-databind" % "2.9.7",
      "org.immutables" % "value" % "2.7.3"
    )
  )
