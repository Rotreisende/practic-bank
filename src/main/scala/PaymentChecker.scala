import PaymentParticipant.ParticipantCommand
import akka.actor.typed.scaladsl.{ActorContext, Behaviors}
import akka.actor.typed.{ActorRef, Behavior}

object PaymentChecker {
  sealed trait PaymentCommand
  case class PaymentData(from: String, to: String, sum: Long) extends PaymentCommand
  case class CheckPayment(record: String) extends PaymentCommand
  case class FailCheck(record: String) extends PaymentCommand
  case class StopPayment(from: String, to: String) extends PaymentCommand

  def apply(): Behavior[PaymentCommand] =
    Behaviors.setup { context =>
      import PaymentParticipant._

      Behaviors.receiveMessage {
        case CheckPayment(record) =>
          parse(record) match {
            case _@PaymentData(from, to, sum) =>
              val fromParticipant = getOrCreateParticipant(from, context)
              fromParticipant ! CheckSum(sum, from, to, context.self)
              Behaviors.same

            case value: FailCheck =>
              val incorrectPayment = context.spawn(LogIncorrectPayment(), "LogIncorrectPayment")
              incorrectPayment ! value
              Behaviors.same
          }

        case StopPayment(from, to) =>
          val fromParticipant = getOrCreateParticipant(from, context)
          val toParticipant = getOrCreateParticipant(to, context)

          context.log.info(s"Платеж отменен для операции $from -> $to")
          Behaviors.same

        case PaymentData(from, to, sum) =>
          val fromParticipant = getOrCreateParticipant(from, context)
          val toParticipant = getOrCreateParticipant(to, context)

          fromParticipant ! Payment(NegativeSign, sum, context.self)
          toParticipant ! Payment(PositiveSign, sum, context.self)

          fromParticipant ! ShowData
          toParticipant ! ShowData

          context.log.info(s"Перевод $from -> $to на сумму $sum был успешно выполнен")
          Behaviors.same
      }
    }

  private def parse(payment: String): PaymentCommand = {
    val pattern = """(\D+),(\D+),(\d+)""".r
    payment.replaceAll("\\s", "") match {
      case pattern(from, to, sum) =>
        if (sum.toLong > 0) PaymentData(from, to, sum.toLong)
        else FailCheck(payment)
      case _ => FailCheck(payment)
    }
  }

  private def getOrCreateParticipant(name: String, context: ActorContext[PaymentCommand]): ActorRef[ParticipantCommand] = {
    if (context.child(name).isEmpty) {
      context.spawn(PaymentParticipant(name, Storage.get(name)), name)
    } else {
      context.child(name).get.unsafeUpcast[ParticipantCommand]
    }
  }
}
