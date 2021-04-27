import Dependencies.Akka._

name := "akka-quickstart-scala"

version := "1.0"

scalaVersion := "2.13.1"

lazy val akkaVersion = "2.6.14"

libraryDependencies ++= Seq(
  actorTyped,
  testKit,
  logback,
  scalaTest,
  akkaStream
)
