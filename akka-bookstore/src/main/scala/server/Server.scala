package server

import java.io.File

import akka.actor.{ActorSystem, Props}
import com.typesafe.config.ConfigFactory

object Server {
  def main(args: Array[String]): Unit = {
    val configFile = new File(getClass.getResource("/server.conf").toURI)
    val config = ConfigFactory.parseFile(configFile)
    val system = ActorSystem.create("server_system", config)

    val serverActor = system.actorOf(Props[StoreActor], "store_actor")
  }
}
