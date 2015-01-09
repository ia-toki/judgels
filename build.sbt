import de.johoop.testngplugin.TestNGPlugin
import de.johoop.jacoco4sbt.JacocoPlugin.jacoco

name := """gabriel-commons"""

version := "1.0-SNAPSHOT"

lazy val gabrielcommons = (project in file(".")).enablePlugins(PlayJava).disablePlugins(plugins.JUnitXmlReportPlugin)

scalaVersion := "2.11.1"

libraryDependencies ++= Seq(
  javaJdbc,
  javaWs,
  javaJpa.exclude("org.hibernate.javax.persistence", "hibernate-jpa-2" +
    ".0-api"),
  filters,
  cache,
  "org.hibernate" % "hibernate-entitymanager" % "4.3.7.Final",
  "commons-io" % "commons-io" % "2.4",
  "com.fasterxml.jackson.module" % "jackson-module-scala" % "2.0.2",
  "com.google.guava" % "guava" % "r05",
  "mysql" % "mysql-connector-java" % "5.1.26",
  "org.jsoup" % "jsoup" % "1.7.2",
  "org.apache.poi" % "poi" % "3.10-FINAL",
  "com.google.inject" % "guice" % "3.0",
  "org.eclipse.jgit" % "org.eclipse.jgit" % "3.4.1.201406201815-r",
  "org.webjars" % "angularjs" % "1.3.1",
  "org.webjars" % "angular-loading-bar" % "0.6.0",
  "org.webjars" % "ckeditor" % "4.4.1",
  "org.webjars" % "coffee-script" % "1.8.0",
  "org.webjars" % "highcharts" % "4.0.4",
  "org.webjars" % "html5-desktop-notifications" % "1.0.1",
  "org.webjars" % "jquery-history" % "0.1-1",
  "org.webjars" % "jquery-placeholder" % "2.0.7",
  "org.webjars" % "jquery" % "2.1.1",
  "org.webjars" % "jquery-ui" % "1.11.1",
  "org.webjars" % "jshint" % "12",
  "org.webjars" % "less" % "1.7.5",
  "org.webjars" % "prettify" % "4-Mar-2013",
  "org.webjars" % "requirejs" % "2.1.15",
  "com.puppycrawl.tools" % "checkstyle" % "6.1",
  "com.adrianhurt" %% "play-bootstrap3" % "0.3",
  "com.nimbusds" % "c2id-server-sdk" % "2.0"
)

TestNGPlugin.testNGSettings

TestNGPlugin.testNGSuites := Seq("testng.xml")

TestNGPlugin.testNGOutputDirectory := "target/testng"

jacoco.settings

parallelExecution in jacoco.Config := false

includeFilter in (Assets, LessKeys.less) := "*.less"

excludeFilter in (Assets, LessKeys.less) := "_*.less"

javaOptions in Test ++= Seq(
  "-Dconfig.resource=test.conf"
)

javacOptions ++= Seq("-s", "app")

javacOptions ++= Seq("-Xlint:unchecked")
