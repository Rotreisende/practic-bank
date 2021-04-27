package com.example

import akka.actor.typed.SupervisorStrategy
import akka.actor.typed.scaladsl.Behaviors
import akka.actor.typed.{ActorSystem, Behavior}
import akka.stream.scaladsl.{FileIO, Framing, Sink}
import akka.stream.{ActorMaterializer, Materializer, SystemMaterializer}
import akka.util.ByteString
import com.example.Main.SendMessage

import scala.collection.JavaConverters._
import java.io.{File, IOException}
import java.nio.file.{Files, Paths}
import akka.stream.scaladsl._

object PaymentsReader{
  sealed trait PaymentSign
  case object PositiveSign extends PaymentSign
  case object NegativeSign extends PaymentSign

  case class CheckPayment(payment: String)

  def apply(): Behavior[SendMessage] = {
    Behaviors
      .supervise[SendMessage](upload())
      .onFailure[Exception](SupervisorStrategy.restart)
  }

  def upload(): Behavior[SendMessage] = Behaviors.receive { (context, message) =>
    message match {
      case SendMessage(root, mask) => {
        val checkerActor = context.spawn(PaymentChecker.apply, "Checker")
        implicit val system = context.system

        val files = Files
          .list(Paths.get(root))
          .filter(_.getFileName.toString.endsWith(mask))
          .iterator().asScala

        files.foreach(item => context.log.info("root: " + item))

        Source
          .fromIterator(() => files)
            .flatMapConcat{
              FileIO
                .fromPath(_)
                .via(Framing.delimiter(ByteString("\n"), 256, true))
                .map(bs => bs.utf8String)
            }
            .to(Sink.foreach[String] { byteString =>
              context.log.info(s"root: $byteString")
              checkerActor ! CheckPayment(byteString)
            })
            .run()

        Behaviors.same
      }
    }
  }
}
