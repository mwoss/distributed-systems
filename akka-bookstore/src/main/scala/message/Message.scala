package message

import akka.actor.ActorRef

case class SearchRequest(bookTitle: String) extends Serializable
case class OrderRequest(bookTitle: String) extends Serializable
case class StreamRequest(bookTitle: String) extends Serializable

sealed trait SearchResponse{
  val hash: Int
}
case class SearchResponsePositive(bookTitle: String, price: String, hash: Int) extends SearchResponse with Serializable
case class SearchResponseNegative(hash: Int) extends SearchResponse with Serializable

case class FindBookRequest(bookTitle: String, dbID: Int, hash: Int) extends Serializable
case class WriteOrderRequest(bookTitle: String)

case class BookFound(bookTitle: String, price: String) extends Serializable
case class BookNotFound() extends Serializable

case class OrderSuccess(bookTitle: String) extends Serializable
case class OrderFailure() extends Serializable

case class StreamResponse(text: String) extends Serializable