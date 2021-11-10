import com.typesafe.config.ConfigFactory

import scala.collection.mutable

object Storage {
  private val participants = mutable.Map[String, Long]()
  apply()

  private def apply(): Unit = {
    val factory = ConfigFactory.load()
    val str = factory.getString("participants")
    str.split(",").foreach { x =>
      val arr = x.split(":")
      participants(arr(0)) = arr(1).toLong
    }
  }

  def getAll: mutable.Map[String, Long] = {
    participants
  }

  def get(name: String): Long = {
    participants(name)
  }

  def add(name: String, value: Long): Unit = {
    participants(name) = value
  }
}
