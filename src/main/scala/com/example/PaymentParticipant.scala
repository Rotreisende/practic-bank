package com.example

import akka.actor._
import akka.actor.typed.{ActorRef, ActorSystem, Behavior}
import akka.actor.typed.scaladsl.Behaviors
import com.example.PaymentsReader.{PaymentSign, PositiveSign, NegativeSign}

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
