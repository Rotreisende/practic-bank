package com.example

import akka.actor.typed.{ActorSystem, Behavior, SupervisorStrategy}
import akka.actor.typed.scaladsl.Behaviors
import akka.stream.scaladsl.{FileIO, Framing, Sink}
import akka.util.ByteString
import com.example.Main.SendMessage
import akka.stream.alpakka.csv.scaladsl.CsvParsing

import scala.collection.JavaConverters._
import java.nio.file.{Files, Path, Paths}
import akka.stream.scaladsl._

import scala.collection.convert.ImplicitConversions.`iterator asScala`

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
          .iterator()

        files.foreach(println)

        Source
          .fromIterator(() => Iterator(Paths.get("src/main/resources/csv/payments.csv")))
            .flatMapConcat{
              FileIO
                .fromPath(_)
                .via(Framing.delimiter(ByteString("\n"), 128, allowTruncation = true))
            }
            .to(Sink.foreach[ByteString] { byteString =>
              println(byteString.utf8String)
              checkerActor ! CheckPayment(byteString.utf8String)
            })
            .run()

        Behaviors.same
      }
    }
  }
}
