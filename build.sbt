import Dependencies.Akka._

name := "akka-quickstart-scala"

version := "1.0"

scalaVersion := "2.13.1"

val AkkaVersion = "2.6.14"
val JacksonVersion = "2.11.4"

libraryDependencies ++= Seq(
  actorTyped,
  testKit,
  logback,
  scalaTest,
  akkaStream,
  csvAlpakka,
  "com.typesafe.akka" %% "akka-stream-kafka" % "2.1.0",
  "com.typesafe.akka" %% "akka-stream" % AkkaVersion,
  "com.fasterxml.jackson.core" % "jackson-databind" % JacksonVersion
)
