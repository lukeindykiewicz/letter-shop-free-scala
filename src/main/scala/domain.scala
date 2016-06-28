package lettershop.free

case class Cart(letters: String)
case class Price(price: Double)
case class Checkout(price: Double, receiptId: String)
case class ReceiptHistory(price: Double, receiptId: String, letters: String)

import akka.http.scaladsl.marshallers.sprayjson.SprayJsonSupport
import spray.json._

object CartJsonSupport extends SprayJsonSupport with DefaultJsonProtocol {
  implicit val CartFormats = jsonFormat1(Cart)
}

object PriceJsonSupport extends SprayJsonSupport with DefaultJsonProtocol {
  implicit val PriceFormats = jsonFormat1(Price)
}

object CheckoutJsonSupport extends SprayJsonSupport with DefaultJsonProtocol {
  implicit val CheckoutFormats = jsonFormat2(Checkout)
}

object ReceiptHistoryJsonSupport extends SprayJsonSupport with DefaultJsonProtocol {
  implicit val ReceiptHistoryFormats = jsonFormat3(ReceiptHistory)
}

sealed trait StorageADT[A]
case class AddToCart(cartId: String, letters: String) extends StorageADT[Unit]
case class GetCart(cartId: String)                    extends StorageADT[Cart]
case class RemoveCart(cartId: String)                 extends StorageADT[Unit]
case class AddToPrices(letter: String, price: Double) extends StorageADT[Unit]
case object GetPrices extends StorageADT[Map[String, Price]]
case class GetPricesForLetters(letters: String)                   extends StorageADT[Map[String, Price]]
case class AddToReceipts(cartId: String, receipt: ReceiptHistory) extends StorageADT[Unit]
case object GetReceipts extends StorageADT[List[ReceiptHistory]]
case class BasePrice(letters: String) extends StorageADT[Double]

sealed trait PriceADT[A]
case class CountThreeForTwo(letters: String, prices: Map[String, Price])
    extends PriceADT[Double => Double]
case class CountPromoCode(promoCode: Option[String]) extends PriceADT[Double => Double]

// sealed trait ValidADT[A]
// IsLetter
// IsMoreThanZero
