package com.example

import akka.actor.typed.{ActorSystem, Behavior}
import akka.actor.typed.scaladsl.Behaviors
import com.typesafe.config.{Config, ConfigFactory}

object Main extends App{
  case class SendMessage(root: String, mask: String)
  case class Factory(config: Config = ConfigFactory.load())

  val helloSayer: Behavior[SendMessage] = Behaviors.setup { context =>
    Behaviors.receiveMessage {
      case SendMessage(dir, mask) => {
        context.spawn(PaymentsReader.apply(), "checker") ! SendMessage(dir, mask)
        Behaviors.same
      }
    }
  }

  val mask = Factory().config.getString("mask")
  val root = Factory().config.getString("root")
  val helloSystem: ActorSystem[SendMessage] = ActorSystem(helloSayer, "root")
  helloSystem ! SendMessage(root, mask)
}
