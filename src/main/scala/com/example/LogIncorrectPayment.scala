package com.example

import akka.actor.typed.Behavior
import akka.actor.typed.scaladsl.Behaviors
import com.example.PaymentsReader.CheckPayment

object LogIncorrectPayment {
  def apply: Behavior[CheckPayment] = Behaviors.receive { (context, message) =>
      message match {
        case CheckPayment(payment) =>
          context.log.info(payment + "- incorrect payment")
          Behaviors.same
      }
  }
}
