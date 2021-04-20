package com.example

import akka.actor.typed.{ActorSystem, Behavior}
import akka.actor.typed.scaladsl.Behaviors
import com.example.PaymentsReader.SendMessage
import com.typesafe.config.ConfigFactory

object Main extends App{
  final case class StartWork(directory: String, mask: String)

  val helloSayer: Behavior[StartWork] = Behaviors.setup { context =>
    Behaviors.receiveMessage {
      case StartWork(dir, mask) => {
        context.spawn(PaymentsReader.checker, "checker") ! SendMessage(dir, mask)
        Behaviors.same
      }
    }
  }

  val factory = ConfigFactory.load()
  val mask = factory.getString("mask")
  val root = factory.getString("root")
  val helloSystem: ActorSystem[StartWork] = ActorSystem(helloSayer, "root")
  helloSystem ! StartWork(root, mask)
}
