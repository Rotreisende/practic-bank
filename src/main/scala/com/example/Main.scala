package com.example

import akka.actor.typed.{ActorSystem, Behavior}
import akka.actor.typed.scaladsl.Behaviors
import com.typesafe.config.{Config, ConfigFactory}

import scala.util.Try

case class PaymentConfig(catalog: String, mask:String)

object PaymentConfig {
  def apply(rootConfig: Config): PaymentConfig = {
    val mask = rootConfig.getString("mask")
    val catalog = rootConfig.getString("root")
    new PaymentConfig(catalog, mask)
  }
}

object Main extends App{
  case class SendMessage(root: String, mask: String)

  val helloSayer: Behavior[SendMessage] = Behaviors.setup { context =>
    Behaviors.receiveMessage {
      case SendMessage(dir, mask) => {
        context.spawn(PaymentsReader.apply(), "checker") ! SendMessage(dir, mask)
        Behaviors.same
      }
    }
  }

  val helloSystem: ActorSystem[SendMessage] = ActorSystem(helloSayer, "root")

  def safelyReadConfig(): Try[PaymentConfig] = {
    Try {
      PaymentConfig(ConfigFactory.load())
    }
  }

  def unsafeRun(paymentConfig: PaymentConfig) = {
    helloSystem ! SendMessage(paymentConfig.catalog, paymentConfig.mask)
  }

  safelyReadConfig().fold(_ => helloSystem.terminate(), unsafeRun)
}
