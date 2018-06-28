package client

import java.io.File

import akka.actor.{ActorRef, ActorSystem, Props}
import com.typesafe.config.ConfigFactory
import message.{OrderRequest, SearchRequest, StreamRequest}

import scala.io.StdIn.readLine
import scala.util.control.Breaks._

object Client{
  def main(args: Array[String]): Unit = {
    val configFile = new File(getClass.getResource("/client.conf").toURI)
    val config = ConfigFactory.parseFile(configFile)
    val system = ActorSystem.create("client_system", config)

    println("Hi. Can you insert your nickname? :)")
    val clientName = readLine("Your name: ")
    val clientActor = system.actorOf(Props[ClientActor], clientName)

    println("Available options: search [book-name], order [book-name], stream [book-name]")
    breakable {
      while (true) {
        var data: String = readLine()
        data match {
          case req if data.startsWith("search") => clientActor ! SearchRequest(data.split(" ")(1))
          case req if data.startsWith("order") => clientActor ! OrderRequest(data.split(" ")(1))
          case req if data.startsWith("stream") => clientActor ! StreamRequest(data.split(" ")(1))
          case "q" => break
          case _ => println("Unrecognized command")
        }
      }
    }
    system.terminate()
  }
}
