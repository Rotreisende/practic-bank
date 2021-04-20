package com.example

import akka.actor.ClassicActorSystemProvider
import akka.actor.typed.scaladsl.Behaviors
import akka.actor.typed.{ActorSystem, Behavior}
import akka.util.ByteString

import java.io.File
import java.nio.file.Paths

object PaymentsReader{
  sealed trait PaymentSign
  case class PositiveSign() extends PaymentSign
  case class NegativeSign() extends PaymentSign

  case class CheckPayment(payment: String)
  case class SendMessage(root: String, mask: String)

  val checker: Behavior[SendMessage] = Behaviors.setup { context =>
    val checkerActor = context.spawn(PaymentChecker.check, "Checker")
    Behaviors.receiveMessage {
      case SendMessage(root, mask) => {
        implicit val actorSystem: ActorSystem[SendMessage] = ActorSystem(checker, "readerSystem")
        implicit def matFromSystem(implicit provider: ClassicActorSystemProvider): Materializer =
          SystemMaterializer(provider.classicSystem).materializer

        val files = new File(root).listFiles.filter(_.getName.endsWith(mask))

        val names: Seq[String] = files.map(x => x.getName)

        names.foreach { x =>
          FileIO.fromPath(Paths.get(root.concat(x)))
            .via(Framing.delimiter(ByteString("\n"), 256, true).map(_.utf8String))
            .to(Sink.foreach((x: String) => checkerActor ! CheckPayment(x)))
            .run()
        }

        Behaviors.same
      }
    }
  }

  val reader: Behavior[StartWork] = Behaviors.setup { context =>
    val reader = context.spawn(PaymentsReader.checker, "paymentReader")
    Behaviors.receiveMessage {
      case StartWork(root, mask) => {
        reader ! SendMessage(root, mask)
        Behaviors.same
      }
    }
  }
}
