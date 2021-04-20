package com.example

import akka.actor.typed.Behavior
import akka.actor.typed.scaladsl.Behaviors
import com.example.PaymentsReader.{CheckPayment}

object PaymentChecker {

  def check: Behavior[CheckPayment] = Behaviors.setup {context =>
    Behaviors.receiveMessage {
      case CheckPayment(payment) => {
        context.log.info("Пришло на проверку: " + payment)
        val seq = parse(payment)
        context.log.info("После парсинга: " + seq)
        Behaviors.same
      }
    }
  }

  def parse(string: String): Seq[String] = {
    val pattern = """(\D+),(\D+),(\d+)""".r
    string match {
      case pattern(name1, name2, sum) => Seq(name1, name2, sum)
    }
  }
}
