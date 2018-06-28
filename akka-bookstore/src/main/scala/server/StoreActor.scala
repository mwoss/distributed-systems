package server

import java.io.FileNotFoundException
import java.io.IOException

import akka.actor.SupervisorStrategy.{Escalate, Restart}
import akka.actor.{Actor, OneForOneStrategy, Props, SupervisorStrategy}
import akka.event.Logging
import akka.routing.FromConfig
import message.{OrderRequest, SearchRequest, StreamRequest}
import server.workers.order.OrderActor
import server.workers.search.SearchActor
import server.workers.stream.StreamActor

import scala.concurrent.duration._


class StoreActor extends Actor {

  val logger = Logging(context.system, this)

  val searchActor = context.actorOf(FromConfig.props(Props[SearchActor]), "search_actor")
  val orderActor = context.actorOf(Props[OrderActor], "order_actor")
  val streamActor = context.actorOf(Props[StreamActor], "stream_actor")

  override def receive: Receive = {
    case request: SearchRequest => searchActor.tell(request, sender)
    case request: OrderRequest => orderActor.tell(request, sender)
    case request: StreamRequest => streamActor.tell(request, sender)
    case _ => println("Received unknown message")
  }

  override def supervisorStrategy: SupervisorStrategy = {
    OneForOneStrategy(maxNrOfRetries = 10, withinTimeRange = 1 minute) {
      case _: FileNotFoundException => Restart
      case _: IOException => Restart
      case _: Exception => Escalate
    }
  }
}
