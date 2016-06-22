package lettershop

package object free {
  import cats.free.Free

  type Storage[A] = Free[StorageADT, A]
  type Promotion[A] = Free[PromoADT, A]
}

package free {

  import cats.free.Free.liftF

  trait StorageConstructors {
    def addToCart(cartId: String, letters: String): Storage[Unit] =
      liftF[StorageADT, Unit](AddToCart(cartId, letters))
    def getCart(cartId: String): Storage[Cart] =
      liftF[StorageADT, Cart](GetCart(cartId))
    def addToPrices(letter: String, price: Double): Storage[Unit] =
      liftF[StorageADT, Unit](AddToPrices(letter, price))
    def getPrices: Storage[Map[String, Price]] =
      liftF[StorageADT, Map[String, Price]](GetPrices)
    def getReceipts: Storage[ReceiptHistory] =
      liftF[StorageADT, ReceiptHistory](GetReceipts)
  }

  trait PromoConstructors {
    def countPromotion(letters: String, promoCode: String): Promotion[Double] =
      liftF[PromoADT, Double](CountPromotion(letters, promoCode))
  }

}
