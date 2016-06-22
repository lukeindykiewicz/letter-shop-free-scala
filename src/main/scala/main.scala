package lettershop.free

import akka.actor._
import akka.http.scaladsl.Http
import akka.http.scaladsl.server.Directives._
import akka.http.scaladsl.model.StatusCodes.OK
import akka.http.scaladsl.marshallers.sprayjson.SprayJsonSupport._
import akka.stream.ActorMaterializer

object Main
    extends App
    with Routes
    with Programs
    with Compilers {

  implicit val system = ActorSystem("letter-shop-free")
  implicit val materializer = ActorMaterializer()
  implicit val executionContext = system.dispatcher

  val bindingFuture = Http().bindAndHandle(route, "localhost", 8080)
}


trait Routes {
  self: Programs with Compilers =>

  lazy val route =
    pathPrefix("cart") {
      getCart ~ putCart ~ postCart
    }
  // ~
  //     putPrice ~
  //     checkCart ~
  //     checkoutCart ~
  //     getReceipts

  import spray.json.DefaultJsonProtocol._
  implicit val cartJsonFormat = CartJsonSupport.CartFormats
  implicit val priceJsonFormat = PriceJsonSupport.PriceFormats
  implicit val checkoutJsonFormat = CheckoutJsonSupport.CheckoutFormats
  implicit val receiptHistoryFormat = ReceiptHistoryJsonSupport.ReceiptHistoryFormats

  val storageCmp = storageCompiler

  lazy val getCart =
    get {
      path(Segment) { cartId =>
        complete( getCartProgram(cartId).foldMap(storageCmp)  )
      }
    }

  lazy val putCart =
    put {
      path(Segment / Segment) { (cartId, letters) =>
        addToCartProgram(cartId, letters).foldMap(storageCmp)
        complete(OK)
      } ~
      path(Segment) { cartId =>
        addToCartProgram(cartId, "").foldMap(storageCmp)
        complete(OK)
      }
    }

  lazy val postCart =
    post {
      path(Segment / Segment) { (cartId, letters) =>
        updateCartProgram(cartId, letters).foldMap(storageCmp)
        complete(OK)
      }
    }

}
