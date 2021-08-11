import akka.actor.typed.Behavior
import akka.actor.typed.scaladsl.Behaviors

object LogIncorrectPayment {
  import PaymentChecker._

  def apply(): Behavior[FailCheck] =
    Behaviors.setup { context =>
      Behaviors.receiveMessage {
        case FailCheck(record) =>
          context.log.info(s"Incorrect record $record")
          Behaviors.same
      }
    }
}
