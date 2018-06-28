package client

import akka.actor.{Actor, ActorRef}
import akka.event.Logging
import message._


class ClientActor extends Actor {

  val logger = Logging(context.system, this)
  var server = context.actorSelection("akka.tcp://server_system@127.0.0.1:3552/user/store_actor")

  override def receive: Receive = {
    case request: SearchRequest => server.tell(request, self)
    case request: OrderRequest => server.tell(request, self)
    case request: StreamRequest => server.tell(request, self)

    case BookFound(bookTitle, price) => println(s"Book found in db. Title: $bookTitle, price: $price")
    case BookNotFound => println("Book wasn't found in db.")
    case OrderFailure => println("Order cannot be completed. No such book in store.")
    case OrderSuccess(bookTitle) => println(s"You order $bookTitle has been successfully added to order database.")
    case StreamResponse(text) => println(text)

    case _ => println("Received unknown message")
  }
}
