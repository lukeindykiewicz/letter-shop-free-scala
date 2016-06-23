package lettershop.free

trait Programs {

  import cats.free.Free

  def getCartProgram(cartId: String)
    (implicit S: StorageConstructors[LetterShop]): Free[LetterShop, Cart] = {
    import S._
    for {
      cart <- getCart(cartId)
    } yield cart
  }

  def addToCartProgram(cartId: String, letters: String)
    (implicit S: StorageConstructors[LetterShop]): Free[LetterShop, Unit] = {
    import S._
    for {
      _ <- addToCart(cartId, letters)
    } yield ()
  }
  
  def updateCartProgram(cartId: String, letters: String)
    (implicit S: StorageConstructors[LetterShop]): Free[LetterShop, Unit] = {
    import S._
    for {
      old <- getCart(cartId)
      _ <- addToCart(cartId, old.letters + letters)
    } yield ()
  }

  def addToPricesProgram(letter: String, price: Price)
    (implicit S: StorageConstructors[LetterShop]): Free[LetterShop, Unit] = {
    import S._
    for {
      _ <- addToPrices(letter, price.price)
    } yield ()
  }

  def checkCartProgram(cartId: String, promoCode: Option[String])
    (implicit S: StorageConstructors[LetterShop], P: PromoConstructors[LetterShop]):
      Free[LetterShop, Price] = {
    import S._
    import P._
    for {
      cart <- getCart(cartId)
      base <- basePrice(cart.letters)
      prices <- getPricesForLetters(cart.letters)
      promo <- countPromotion(cart.letters, promoCode, prices)
    } yield Price(promo(base))
  }

  private def countPromotion(letters: String, promoCode: Option[String], prices: Map[String, Price])
    (implicit P: PromoConstructors[LetterShop]): Free[LetterShop, Double => Double] = {
    import P._
    for {
      promo1 <- countThreeForTwo(letters, prices)
      promo2 <- countPromoCode(promoCode)
    } yield (promo1 andThen promo2)
  }

}

trait Compilers {
 self: PromotionService =>

  import cats.{Id, ~>}

  def storageCompiler: StorageADT ~> Id =
    new (StorageADT ~> Id) {

      import scala.collection.concurrent.TrieMap
      var carts: TrieMap[String, String] = TrieMap.empty
      var prices: TrieMap[String, Price] = TrieMap.empty
      var receipts: TrieMap[String, ReceiptHistory] = TrieMap.empty

      private val defaultPrice = Price(10)
      private def p(x: String) = prices.getOrElse(x, defaultPrice)

      def apply[A](fa: StorageADT[A]): Id[A] = fa match {
        case GetCart(cartId) => Cart(carts.getOrElse(cartId, ""))
        case AddToCart(cartId, letters) =>
          carts += (cartId -> letters)
          ()
        case AddToPrices(letter, price) =>
          prices += (letter -> Price(price))
          ()
        case BasePrice(letters) => letters.map(x => p(x.toString).price).sum
        case GetPrices =>  prices.toMap.asInstanceOf[A]
        case GetPricesForLetters(letters) =>
          letters.toSeq.map(_.toString).map(x => x -> p(x)).toMap.asInstanceOf[A]
        case _ =>
          println("Not implemented yet")
          "not implemented".asInstanceOf[A]
      }

    }


  def priceCompiler: PriceADT ~> Id =
    new (PriceADT ~> Id) {
      def apply[A](fa: PriceADT[A]): Id[A] = fa match {
        case CountThreeForTwo(letters, prices) =>
          countThreeForTwo(letters, prices)
        case CountPromoCode(promoCode) =>
          countPromo(promoCode)
      }
    }

  def compiler: LetterShop ~> Id = storageCompiler or priceCompiler

}
