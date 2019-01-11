resolvers += "Typesafe repository" at "http://repo.typesafe.com/typesafe/releases/"

// Play Framework plugin
addSbtPlugin("com.typesafe.play" % "sbt-plugin" % "2.4.6")

addSbtPlugin("com.typesafe.sbt" % "sbt-play-enhancer" % "1.1.0")

// SBT Web plugins
addSbtPlugin("com.typesafe.sbt" % "sbt-less" % "1.0.6")

addSbtPlugin("com.typesafe.sbt" % "sbt-jshint" % "1.0.1")

addSbtPlugin("com.typesafe.sbt" % "sbt-rjs" % "1.0.1")

// Testing plugins
addSbtPlugin("de.johoop" % "sbt-testng-plugin" % "3.0.2")

addSbtPlugin("de.johoop" % "jacoco4sbt" % "2.1.6")

addSbtPlugin("com.eed3si9n" % "sbt-buildinfo" % "0.3.2")
