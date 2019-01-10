resolvers += "Typesafe repository" at "http://repo.typesafe.com/typesafe/releases/"

// Play Framework plugin
addSbtPlugin("com.typesafe.play" % "sbt-plugin" % "2.4.3")

addSbtPlugin("com.typesafe.sbt" % "sbt-play-enhancer" % "1.1.0")

// Testing plugins
addSbtPlugin("de.johoop" % "sbt-testng-plugin" % "3.0.2")

addSbtPlugin("de.johoop" % "jacoco4sbt" % "2.1.6")
