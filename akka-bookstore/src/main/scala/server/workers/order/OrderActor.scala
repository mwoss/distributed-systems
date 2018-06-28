package server.workers.order

import java.io.{File, FileWriter}
import java.nio.file.Paths

import akka.pattern.ask
import akka.actor.{Actor, Props}
import akka.util.Timeout

import scala.concurrent.duration._
import scala.util.{Failure, Success}
import message._

import scala.concurrent.ExecutionContextExecutor

class OrderActor extends Actor {

  implicit val duration: Timeout = 10 seconds
  private implicit val executionContext: ExecutionContextExecutor = context.system.dispatcher

  val searchActor = context.actorSelection("/user/store_actor/search_actor")
  val orderPath = "./src/main/resources/orders"

  override def receive: Receive = {
    case request: OrderRequest =>
      val client = sender
      searchActor.ask(SearchRequest(request.bookTitle)).onComplete {
        case Success(status) =>
          status match {
            case BookFound(bookTitle, _) =>
              val orders = new FileWriter(new File(Paths.get(orderPath).toUri),true)
              orders.append(bookTitle + "\n")
              orders.close()
              client ! OrderSuccess(bookTitle)
            case BookNotFound => client ! OrderFailure
          }
        case Failure(_) => client ! OrderFailure
      }
  }
}
