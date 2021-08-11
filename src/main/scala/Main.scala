import akka.actor.typed.scaladsl.Behaviors
import akka.actor.typed.{ActorSystem, Behavior}
import com.typesafe.config.{Config, ConfigFactory}

import scala.util.Try

object Main extends App {
  case class DataConfig(catalog: String, mask: String)

  case class Start(catalog: String, mask: String)

  object DataConfig {
    def apply(rootConfig: Config): DataConfig = {
      val mask = rootConfig.getString("mask")
      val catalog = rootConfig.getString("root")
      new DataConfig(catalog, mask)
    }
  }

  val system: ActorSystem[Start] = ActorSystem(Main(), "AkkaStream")

  def apply(): Behavior[Start] =
    Behaviors.setup { context =>
      Behaviors.receiveMessage {
        case Start(catalog, mask) =>
          val replyTo = context.spawn(PaymentsReader(), "reader")
          replyTo ! DataConfig(catalog, mask)
          Behaviors.same
      }
    }

  def safelyReadConfig(): Try[DataConfig] = {
    Try {
      DataConfig(ConfigFactory.load())
    }
  }

  def unsafeRun(dataConfig: DataConfig) = {
    system ! Start(dataConfig.catalog, dataConfig.mask)
  }

  safelyReadConfig().fold(_ => system.terminate(), unsafeRun)
}
