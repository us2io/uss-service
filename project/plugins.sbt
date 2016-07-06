// The Play plugin
addSbtPlugin("com.typesafe.play" % "sbt-plugin" % "2.5.4")

// Packaging
addSbtPlugin("com.typesafe.sbt" % "sbt-native-packager" % "1.0.0-RC1")

// Monitoring and instrumentation
addSbtPlugin("io.kamon" % "aspectj-play-runner" % "0.1.3")

// Bytecode bug checking
addSbtPlugin("uk.co.josephearl" % "sbt-findbugs" % "2.4.1")

// Source level bug checking
libraryDependencies ++= Seq(
  "net.sourceforge.pmd" % "pmd-java8" % "5.5.0"
)
