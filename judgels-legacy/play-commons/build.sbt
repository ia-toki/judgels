import de.johoop.testngplugin.TestNGPlugin
import de.johoop.jacoco4sbt.JacocoPlugin.jacoco

lazy val playcommons = (project in file("."))
    .enablePlugins(PlayJava, SbtWeb)
    .disablePlugins(plugins.JUnitXmlReportPlugin)
    .dependsOn(commons)
    .aggregate(commons)
    .settings(
        name := "playcommons",
        version := IO.read(file("version.properties")).trim,
        scalaVersion := "2.11.7",
        libraryDependencies ++= Seq(
            javaJdbc,
            javaWs,
            javaJpa.exclude("org.hibernate.javax.persistence", "hibernate-jpa-2.0-api"),
            filters,
            cache,
            evolutions,
            "javax.inject" % "javax.inject" % "1",
            "org.hibernate" % "hibernate-entitymanager" % "4.3.7.Final",
            "org.springframework" % "spring-context" % "4.1.6.RELEASE",
            "mysql" % "mysql-connector-java" % "5.1.26",
            "com.typesafe.play.modules" %% "play-modules-redis" % "2.4.1",
            "com.adrianhurt" %% "play-bootstrap3" % "0.4.4-P24" exclude("org.webjars", "jquery"),
            "org.webjars" % "bootstrap" % "3.3.4" exclude("org.webjars", "jquery"),
            "org.webjars" % "jquery" % "2.1.4",
            "org.webjars" % "jquery-ui" % "1.11.4" exclude("org.webjars", "jquery"),
            "org.webjars" % "less" % "2.5.0",
            "org.webjars" % "requirejs" % "2.1.18",
            "org.webjars" % "momentjs" % "2.10.3",
            "org.webjars" % "ckeditor" % "4.5.3",
            "org.webjars" % "select2" % "4.0.0-2" exclude("org.webjars", "jquery"),
            "org.webjars" % "Eonasdan-bootstrap-datetimepicker" % "4.7.14" exclude("org.webjars", "bootstrap"),
            "joda-time" % "joda-time" % "2.3",
            "org.seleniumhq.selenium" % "selenium-java" % "2.46.0",
            "org.jsoup" % "jsoup" % "1.8.3",
            "com.googlecode.htmlcompressor" % "htmlcompressor" % "1.4",
            "com.google.apis" % "google-api-services-analytics" % "v3-rev118-1.20.0",
            "org.apache.poi" % "poi" % "3.10-FINAL"
        )
    )
    .settings(TestNGPlugin.testNGSettings: _*)
    .settings(
        aggregate in test := false,
        aggregate in jacoco.cover := false,
        TestNGPlugin.testNGSuites := Seq("test/resources/testng.xml")
    )
    .settings(jacoco.settings: _*)
    .settings(
        parallelExecution in jacoco.Config := false
    )
    .settings(
        excludeFilter in (Assets, LessKeys.less) := "_*.less"
    )
    .settings(
        publishArtifact in (Compile, packageDoc) := false,
        publishArtifact in packageDoc := false,
        sources in (Compile,doc) := Seq.empty
    )

resolvers += "google-sedis-fix" at "http://pk11-scratch.googlecode.com/svn/trunk"

lazy val commons = RootProject(file("../commons"))
