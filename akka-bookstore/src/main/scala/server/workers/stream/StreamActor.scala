package server.workers.stream

import java.io.File

import akka.actor.Actor
import akka.event.Logging
import akka.pattern.ask
import akka.stream.ActorMaterializer
import akka.stream.scaladsl.{Sink, Source}
import akka.util.Timeout
import message._

import scala.concurrent.ExecutionContextExecutor
import scala.util.{Failure, Success}
import scala.concurrent.duration._

class StreamActor extends Actor {

  val logger = Logging(context.system, this)

  implicit val duration: Timeout = 10 seconds
  private implicit val executionContext: ExecutionContextExecutor = context.system.dispatcher
  private implicit val materializer = ActorMaterializer()

  val searchActor = context.actorSelection("/user/store_actor/search_actor")

  override def receive: Receive = {
    case StreamRequest(bookTitle) =>
      val client = sender
      searchActor.ask(SearchRequest(bookTitle)).onComplete {
        case Success(status) =>
          status match {
            case BookFound(bookTitle, _) =>
              Source
                .fromIterator(
                  scala.io.Source
                    .fromFile(new File(getClass.getResource(s"/books/$bookTitle.txt").toURI))
                    .getLines
                )
                .map(line => StreamResponse(line))
                .throttle(1, 1 seconds)
                .runWith(Sink.actorRef(client, StreamResponse("Streaming Ends")))
            case BookNotFound => client ! BookNotFound
          }
        case Failure(_) => client ! BookNotFound
      }
  }
}
