import Main.DataConfig
import PaymentChecker.CheckPayment
import akka.actor.typed.scaladsl.Behaviors
import akka.actor.typed.{ActorSystem, Behavior}
import akka.stream.scaladsl.{FileIO, Framing, Sink, Source}
import akka.util.ByteString

import java.nio.file.{Files, Paths}
import scala.jdk.CollectionConverters.IteratorHasAsScala

object PaymentsReader {
  def apply(): Behavior[DataConfig] =
    Behaviors.setup { context =>
      Behaviors.receiveMessage { message =>
        val replyTo = context.spawn(PaymentChecker(), "checker")
        implicit val system: ActorSystem[Nothing] = context.system

        val files = Files
          .list(Paths.get(message.catalog))
          .filter(_.getFileName.toString.endsWith(message.mask))
          .iterator()
          .asScala

        Source
          .fromIterator(() => files)
          .flatMapConcat {
            FileIO
              .fromPath(_)
              .via(Framing.delimiter(ByteString("\n"), 128, allowTruncation = true))
          }
          .to(Sink.foreach[ByteString] { byteString =>
            replyTo ! CheckPayment(byteString.utf8String)
          })
          .run()

        Behaviors.same
      }
    }
}
