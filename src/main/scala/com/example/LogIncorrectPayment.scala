package com.example

import akka.actor.typed.Behavior
import akka.actor.typed.scaladsl.Behaviors
import com.example.PaymentsReader.CheckPayment

object LogIncorrectPayment {
  val correctPayment: Behavior[CheckPayment] = Behaviors.setup { context =>
    Behaviors.receiveMessage {
      case CheckPayment(payment) =>
        context.log.info(payment + "- incorrect payment")
        Behaviors.same
    }
  }
}
