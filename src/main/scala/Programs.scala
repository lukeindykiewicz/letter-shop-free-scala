package lettershop.free

trait Programs {

  import cats.free.Free
  import java.util.UUID

  def getCartProgram(cartId: String)(
      implicit S: StorageConstructors[LetterShop]): Free[LetterShop, Cart] = {
    import S._
    for {
      cart <- getCart(cartId)
    } yield cart
  }

  def addToCartProgram(cartId: String, letters: String)(
      implicit S: StorageConstructors[LetterShop]): Free[LetterShop, Unit] = {
    import S._
    for {
      _ <- addToCart(cartId, letters)
    } yield ()
  }

  def updateCartProgram(cartId: String, letters: String)(
      implicit S: StorageConstructors[LetterShop]): Free[LetterShop, Unit] = {
    import S._
    for {
      old <- getCart(cartId)
      _   <- addToCart(cartId, old.letters + letters)
    } yield ()
  }

  def addToPricesProgram(letter: String, price: Price)(
      implicit S: StorageConstructors[LetterShop]): Free[LetterShop, Unit] = {
    import S._
    for {
      _ <- addToPrices(letter, price.price)
    } yield ()
  }

  def checkCartProgram(cartId: String, promoCode: Option[String])(
      implicit S: StorageConstructors[LetterShop],
      P: PromoConstructors[LetterShop]): Free[LetterShop, Price] = {
    import S._, P._
    for {
      cart   <- getCart(cartId)
      base   <- basePrice(cart.letters)
      prices <- getPricesForLetters(cart.letters)
      promo  <- countPromotionProgram(cart.letters, promoCode, prices)
    } yield Price(promo(base))
  }

  private def countPromotionProgram(letters: String,
                                    promoCode: Option[String],
                                    prices: Map[String, Price])(
      implicit P: PromoConstructors[LetterShop]): Free[LetterShop, Double => Double] = {
    import P._
    for {
      promo1 <- countThreeForTwo(letters, prices)
      promo2 <- countPromoCode(promoCode)
    } yield (promo1 andThen promo2)
  }

  def checkoutCartProgram(cartId: String, promoCode: Option[String])(
      implicit S: StorageConstructors[LetterShop],
      P: PromoConstructors[LetterShop]): Free[LetterShop, Checkout] = {
    import S._, P._
    val uuid = UUID.randomUUID.toString
    for {
      p    <- checkCartProgram(cartId, promoCode)
      cart <- getCart(cartId)
      _    <- addToReceipts(cartId, ReceiptHistory(p.price, uuid, cart.letters))
      _    <- removeCart(cartId)
    } yield Checkout(p.price, uuid)
  }

  def getReceiptsProgram(
      implicit S: StorageConstructors[LetterShop]): Free[LetterShop, List[ReceiptHistory]] = {
    import S._
    for {
      r <- getReceipts
    } yield r
  }

}
