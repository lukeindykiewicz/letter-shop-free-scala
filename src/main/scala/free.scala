package lettershop

package object free {
  import cats.data.Coproduct
  type LetterShop[A] = Coproduct[StorageADT, PriceADT, A]
}

package free {

  import cats.free.Free
  import cats.free.Inject
  import scala.language.higherKinds

  class StorageConstructors[F[_]](implicit I: Inject[StorageADT, F]) {
    def addToCart(cartId: String, letters: String): Free[F, Unit] =
      Free.inject[StorageADT, F](AddToCart(cartId, letters))
    def getCart(cartId: String): Free[F, Cart] =
      Free.inject[StorageADT, F](GetCart(cartId))
    def addToPrices(letter: String, price: Double): Free[F, Unit] =
      Free.inject[StorageADT, F](AddToPrices(letter, price))
    def getPrices: Free[F, Map[String, Price]] =
      Free.inject[StorageADT, F](GetPrices)
    def getPricesForLetters(letters: String): Free[F, Map[String, Price]] =
      Free.inject[StorageADT, F](GetPricesForLetters(letters))
    def getReceipts: Free[F, ReceiptHistory] =
      Free.inject[StorageADT, F](GetReceipts)
    def basePrice(letters: String): Free[F, Double] =
      Free.inject[StorageADT, F](BasePrice(letters))
  }

  object StorageConstructors {
    implicit def storageConstructors[F[_]](implicit I: Inject[StorageADT, F]): StorageConstructors[F] =
      new StorageConstructors[F]
  }

  class PromoConstructors[F[_]](implicit I: Inject[PriceADT, F]) {
    def countThreeForTwo(letters: String, prices: Map[String, Price]): Free[F, Double => Double] =
      Free.inject[PriceADT, F](CountThreeForTwo(letters, prices))
    def countPromoCode(promoCode: Option[String]): Free[F, Double => Double] =
      Free.inject[PriceADT, F](CountPromoCode(promoCode))
  }

  object PromoConstructors {
    implicit def promoConstructors[F[_]](implicit I: Inject[PriceADT, F]): PromoConstructors[F] =
      new PromoConstructors[F]
  }

}
