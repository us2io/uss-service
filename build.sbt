import uk.co.josephearl.sbt.findbugs.FindBugsPriority.Low

name := "svc-seed"

version := "1.0-SNAPSHOT"

lazy val root = (project in file(".")).enablePlugins(PlayJava)

scalaVersion := "2.11.7"

libraryDependencies ++= {
  val akkaV = "2.4.4"
  val camelV = "2.17.2"
  val awsV = "1.10.52"
  val kamonV = "0.6.2-bda51ea175d734025bf36abb0f8678a5d1c0959d"
  Seq(
    javaJdbc,
    cache,
    javaWs,

    // Akka camel
    "com.typesafe.akka" %% "akka-camel" % akkaV,
//    "org.apache.camel" % "camel-aws" % camelV,

    // Database access
    "org.sql2o" % "sql2o" % "1.5.4",
    "mysql" % "mysql-connector-java" % "5.1.18",

    // AWS APIs
    "com.amazonaws" % "aws-java-sdk-core" % awsV,
    "com.amazonaws" % "aws-java-sdk-s3" % awsV,

    // Instrumentation
    "io.kamon" %% "kamon-core" % kamonV,
    "io.kamon" %% "kamon-play-25" % kamonV,
    "io.prometheus" % "simpleclient_common" % "0.0.14",
    "io.prometheus" % "simpleclient_hotspot" % "0.0.14",
    "io.prometheus" % "simpleclient_logback" % "0.0.14",

    // Helpers and utilities
    "commons-io" % "commons-io" % "2.5",
    "org.projectlombok" % "lombok" % "1.16.8",
    "com.iheart" %% "play-swagger" % "0.2.1-PLAY2.5",
    "org.webjars" % "swagger-ui" % "2.1.4",
    "com.novocode" % "junit-interface" % "0.11" % Test,
    "com.typesafe.akka" %% "akka-testkit" % akkaV % Test,
    "org.assertj" % "assertj-core" % "3.4.1" % Test,
    "org.mockito" % "mockito-all" % "1.10.19" % Test,
    // Java Agent
    "org.aspectj" % "aspectjweaver" % "1.8.9"
  )
}

libraryDependencies ~= {
  _.map(_.exclude("org.slf4j", "slf4j-log4j12"))
}

resolvers ++= Seq(Resolver.jcenterRepo, "Kamon Repository Snapshots" at "http://snapshots.kamon.io")

javacOptions ++= Seq("-Xlint:unchecked", "-parameters")
javaOptions ++= Seq("-Dlogger.resource=dev-logback.xml")

enablePlugins(JavaAppPackaging)
topLevelDirectory := None

PmdSettings.pmdTask

//findbugsSettings
findbugsOnlyAnalyze := Some(Seq("io.us2.svc"))
findbugsFailOnError := true
findbugsPriority := Low

(findbugs in Compile) <<= (findbugs in Compile) triggeredBy (compile in Compile)
compile <<= (compile in Compile).dependsOn(PmdSettings.pmd)
