package com.example

import akka.actor._
import akka.actor.typed.ActorRef
import com.example.PaymentsReader.{NegativeSign, PaymentSign, PositiveSign}

class PaymentParticipant(balance: Long = 0, name: String) extends Actor {
  case class Payment(sign: PaymentSign, value: Long, participant: ActorRef[PaymentParticipant])

  override def receive: Receive = {
    case Payment(sign, value, participant) => {
      sign match {
        case _@PositiveSign() =>


        case _@NegativeSign() =>

      }
    }
  }
}
