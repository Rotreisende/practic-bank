package com.example

import akka.actor.typed.Behavior
import akka.actor.typed.scaladsl.Behaviors
import com.example.PaymentsReader.CheckPayment

object PaymentChecker {
  case class PaymentData(name1: String, name2: String, sum: Long)

  def apply: Behavior[CheckPayment] =
    Behaviors.receive { (context, message) =>
      message match {
        case CheckPayment(payment) => {
          context.log.info(s"Пришло на проверку: $payment")
          val seq = parse(payment)
          context.log.info(s"После парсинга: $seq")
          Behaviors.same
        }
      }
    }

  def parse(payment: String): PaymentData = {
    val pattern = """(\D+),(\D+),(\d+)""".r
    payment.replaceAll("\\s", "") match {
      case pattern(name1, name2, sum) => PaymentData(name1, name2, sum.toLong)
    }
  }
}
