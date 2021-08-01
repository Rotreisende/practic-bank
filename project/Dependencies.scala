import sbt._

object Dependencies {
  private object Version {
    lazy val akka = "2.6.14"
  }

  object Akka {
    lazy val actorTyped = "com.typesafe.akka" %% "akka-actor-typed" % Version.akka
    lazy val testKit = "com.typesafe.akka" %% "akka-actor-testkit-typed" % Version.akka % Test
    lazy val logback = "ch.qos.logback" % "logback-classic" % "1.2.3"
    lazy val scalaTest = "org.scalatest" %% "scalatest" % "3.1.0" % Test
    lazy val akkaStream = "com.typesafe.akka" %% "akka-stream" % Version.akka
    lazy val csvAlpakka =  "com.lightbend.akka" %% "akka-stream-alpakka-csv" % "3.0.1"
  }
}
