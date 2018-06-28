package server.workers.search

import java.util.concurrent.atomic.AtomicInteger

import akka.actor.SupervisorStrategy.Restart
import akka.actor.{Actor, OneForOneStrategy, Props, SupervisorStrategy}
import akka.event.Logging
import message._
import scala.concurrent.duration._

import scala.collection.mutable.ArrayBuffer

class SearchActor extends Actor {

  val logger = Logging(context.system, this)

  val dbSearchActor1 = context.actorOf(Props[DbSearchActor], "db_search_actor_1")
  val dbSearchActor2 = context.actorOf(Props[DbSearchActor], "db_search_actor_2")

  var status = new ArrayBuffer[Boolean]()
  var previous = new ArrayBuffer[Boolean]()
  var hash = new AtomicInteger(0)

  override def receive: Receive = {
    case SearchRequest(bookTitle) =>
      val index = hash.getAndIncrement()
      status.insert(index, false)
      previous.insert(index, false)
      dbSearchActor1.tell(FindBookRequest(bookTitle, 1, index), sender)
      dbSearchActor2.tell(FindBookRequest(bookTitle, 2, index), sender)

    case response: SearchResponse =>
      if(!status(response.hash))
        status(response.hash) = true
      else{
        response match {
          case SearchResponsePositive(bookTitle, price, hash) =>
            if (status(hash)) {
              previous(hash) = true
              sender ! BookFound(bookTitle, price)
            }
          case SearchResponseNegative(hash) =>
            if (status(hash) && !previous(hash))
              sender ! BookNotFound
        }
      }

  }
  override def supervisorStrategy: SupervisorStrategy = {
    OneForOneStrategy(maxNrOfRetries = 3, withinTimeRange = 1 minute) {
      case _: Exception => Restart
    }
  }
}
