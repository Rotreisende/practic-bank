package com.example

import akka.actor.ClassicActorSystemProvider
import akka.actor.typed.scaladsl.Behaviors
import akka.actor.typed.{ActorSystem, Behavior}
import akka.stream.scaladsl.{FileIO, Framing, Sink, Source}
import akka.stream.{ActorMaterializer, Materializer, SystemMaterializer}
import akka.util.ByteString
import com.example.Main.SendMessage
import com.example.PaymentChecker.check

import java.io.File
import java.nio.file.Paths
import akka.stream.scaladsl._

object PaymentsReader{
  sealed trait PaymentSign
  case object PositiveSign extends PaymentSign
  case object NegativeSign extends PaymentSign

  case class CheckPayment(payment: String)


  def apply(): Behavior[Main.SendMessage] = Behaviors.receive { (context, message) =>
    message match {
      case SendMessage(root, mask) => {
        val checkerActor = context.spawn(PaymentChecker.apply, "Checker")
        implicit val system = context.system

        val files: List[File] = new File(root).listFiles.filter(_.getName.endsWith(mask)).toList
        Source
          .fromIterator(() => files.iterator)
            .flatMapConcat{
              FileIO.fromPath(_).via(Framing.delimiter(ByteString("\n"), 256, true))
            }
            .to(Sink.foreach[ByteString] { byteString =>
              checkerActor ! CheckPayment(byteString.toString())
            })
            .run()

        Behaviors.same
      }
    }
  }
}
