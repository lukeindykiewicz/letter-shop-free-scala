package lettershop.free

case class Cart(letters: String)
case class Price(price: Double)
case class Checkout(price: Double, receiptId: String)
case class ReceiptHistory(price: Double, receiptId: String, letters: String)

sealed trait LetterShopADT[A]
case class GetCart(cartId: String) extends LetterShopADT[Cart]
case class UpdateCart(cartId: String, letters: String) extends LetterShopADT[Unit]
case class OverrideCart(cartdId: String, letters: String) extends LetterShopADT[Unit]
case class CheckCart(cartId: String, discountCode: Option[String]) extends LetterShopADT[Price]
case class CheckoutCart(cartId: String, discountCode: Option[String]) extends LetterShopADT[Checkout]
case class PriceTheLetter(letter: String, price: Double) extends LetterShopADT[Unit]
case object GetReceipts extends LetterShopADT[ReceiptHistory]
