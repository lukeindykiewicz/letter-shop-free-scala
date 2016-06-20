package lettershop.free

import akka.actor._
import akka.http.scaladsl.Http
import akka.stream.ActorMaterializer

object Main
    extends App {

  implicit val system = ActorSystem("letter-shop-free")
  implicit val materializer = ActorMaterializer()
  implicit val executionContext = system.dispatcher

  val bindingFuture = Http().bindAndHandle(route, "localhost", 8080)
}
