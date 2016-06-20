package lettershop

package object free {
  import cats.free.Free
  type LetterShop[A] = Free[LetterShopADT, A]
}

package free {

  import cats.free.Free.liftF

  trait LetterShopConstructors {
    def getCart(cartId: String): LetterShop[Cart] =
      liftF[LetterShopADT, Cart](GetCart(cartId))
    def updateCart(cartId: String, letters: String): LetterShop[Unit] =
      liftF[LetterShopADT, Unit](UpdateCart(cartId, letters: String))
    def overrideCart(cartId: String, letters: String): LetterShop[Unit] =
      liftF[LetterShopADT, Unit](OverrideCart(cartId, letters: String))
    def checkCart(cartId: String, discountCode: Option[String]): LetterShop[Price] =
      liftF[LetterShopADT, Price](CheckCart(cartId, discountCode))
    def checkoutCart(cartId: String, discountCode: Option[String]): LetterShop[Checkout] =
      liftF[LetterShopADT, Checkout](CheckoutCart(cartId, discountCode))
    def priceTheLetter(letter: String, price: Double): LetterShop[Unit] =
      liftF[LetterShopADT, Unit](PriceTheLetter(letter, price))
    def getReceipts: LetterShop[ReceiptHistory] =
      liftF[LetterShopADT, ReceiptHistory](GetReceipts)
  }
}
