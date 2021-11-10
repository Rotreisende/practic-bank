import PaymentChecker.{PaymentCommand, PaymentData, StopPayment}
import akka.actor.typed.scaladsl.{AbstractBehavior, ActorContext, Behaviors}
import akka.actor.typed.{ActorRef, Behavior}

object PaymentParticipant {
  sealed trait PaymentSign
  case object PositiveSign extends PaymentSign
  case object NegativeSign extends PaymentSign

  sealed trait ParticipantCommand
  case class Payment(sign: PaymentSign, value: Long, replyTo: ActorRef[PaymentCommand]) extends ParticipantCommand
  case class CheckSum(value: Long, from: String, to: String, replyTo: ActorRef[PaymentCommand]) extends ParticipantCommand
  case object ShowData extends ParticipantCommand

  def apply(name: String, balance: Long): Behavior[ParticipantCommand] =
    Behaviors.setup { context =>
      new PaymentParticipant(context, name, balance)
    }
}

import PaymentParticipant._

private class PaymentParticipant(context: ActorContext[ParticipantCommand],
                                 private val name: String,
                                 private var balance: Long
                                ) extends AbstractBehavior[ParticipantCommand](context) {

  override def onMessage(msg: ParticipantCommand): Behavior[ParticipantCommand] =
    msg match {
      case Payment(sign, value, _) =>
        sign match {
          case _@PositiveSign =>
            balance += value

          case _@NegativeSign =>
            balance -= value
        }
        Behaviors.same

      case ShowData =>
        showData()
        Behaviors.same

      case CheckSum(value, from, to, replyTo) =>
        if (balance - value > 0) {
          replyTo ! PaymentData(from, to, value)
        } else {
          replyTo ! StopPayment(from, to)
        }
        Behaviors.same
    }

  private def showData(): Unit = {
    context.log.info(s"name:$name,balance:$balance")
  }
}