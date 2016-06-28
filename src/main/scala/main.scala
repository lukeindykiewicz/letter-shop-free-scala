package lettershop.free

import akka.actor._
import akka.http.scaladsl.Http
import akka.http.scaladsl.server.Directives._
import akka.http.scaladsl.model.StatusCodes.OK
import akka.http.scaladsl.marshallers.sprayjson.SprayJsonSupport._
import akka.stream.ActorMaterializer

object Main extends App with Routes with Programs with Compilers with PromotionService {

  implicit val system           = ActorSystem("letter-shop-free")
  implicit val materializer     = ActorMaterializer()
  implicit val executionContext = system.dispatcher

  val bindingFuture = Http().bindAndHandle(route, "localhost", 8080)
}

trait Routes { self: Programs with Compilers =>

  import StorageConstructors._
  import PromoConstructors._

  lazy val route =
    pathPrefix("cart") {
      getCart ~ putCart ~ postCart
    } ~
      putPrice ~
      checkCart ~
      checkoutCart ~
      getReceipts

  import spray.json.DefaultJsonProtocol._
  implicit val cartJsonFormat       = CartJsonSupport.CartFormats
  implicit val priceJsonFormat      = PriceJsonSupport.PriceFormats
  implicit val checkoutJsonFormat   = CheckoutJsonSupport.CheckoutFormats
  implicit val receiptHistoryFormat = ReceiptHistoryJsonSupport.ReceiptHistoryFormats

  val cmp = compiler

  lazy val getCart = get {
    path(Segment) { cartId =>
      complete(getCartProgram(cartId).foldMap(cmp))
    }
  }

  lazy val putCart = put {
    path(Segment / Segment) { (cartId, letters) =>
      addToCartProgram(cartId, letters).foldMap(cmp)
      complete(OK)
    } ~
    path(Segment) { cartId =>
      addToCartProgram(cartId, "").foldMap(cmp)
      complete(OK)
    }
  }

  lazy val postCart = post {
    path(Segment / Segment) { (cartId, letters) =>
      updateCartProgram(cartId, letters).foldMap(cmp)
      complete(OK)
    }
  }

  lazy val putPrice = put {
    path("price" / Segment) { letter =>
      entity(as[Price]) { price =>
        addToPricesProgram(letter, price).foldMap(cmp)
        complete(OK)
      }
    }
  }

  lazy val checkCart = get {
    path("check" / Segment) { cartId =>
      parameters("promo".?) { promoCode =>
        val price = checkCartProgram(cartId, promoCode).foldMap(cmp)
        complete(price)
      }
    }
  }

  lazy val checkoutCart = post {
    path("checkout" / Segment) { cartId =>
      parameters("promo".?) { promoCode =>
        val checkout = checkoutCartProgram(cartId, promoCode).foldMap(cmp)
        complete(checkout)
      }
    }
  }

  lazy val getReceipts = get {
    path("receipt") {
      val receipts = getReceiptsProgram.foldMap(cmp)
      complete(receipts)
    }
  }

}
