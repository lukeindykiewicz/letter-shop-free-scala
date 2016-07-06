package lettershop.free

trait Compilers { self: PromotionService =>

  import cats.{Id, ~>}

  def storageCompiler: StorageADT ~> Id =
    new (StorageADT ~> Id) {

      import scala.collection.concurrent.TrieMap
      var carts: TrieMap[String, String]            = TrieMap.empty
      var prices: TrieMap[String, Price]            = TrieMap.empty
      var receipts: TrieMap[String, ReceiptHistory] = TrieMap.empty

      private val defaultPrice = Price(10)
      private def p(x: String) = prices.getOrElse(x, defaultPrice)

      def apply[A](fa: StorageADT[A]): Id[A] = fa match {
        case GetCart(cartId) => Cart(carts.getOrElse(cartId, ""))
        case AddToCart(cartId, letters) =>
          carts += (cartId -> letters)
          ()
        case RemoveCart(cartId) =>
          carts -= cartId
          ()
        case AddToPrices(letter, price) =>
          prices += (letter -> Price(price))
          ()
        case BasePrice(letters) => letters.map(x => p(x.toString).price).sum
        case GetPrices          => prices.toMap.asInstanceOf[A]
        case GetPricesForLetters(letters) =>
          letters.toSeq.map(_.toString).map(x => x -> p(x)).toMap.asInstanceOf[A]
        case AddToReceipts(cartId, receipt) =>
          receipts += cartId -> receipt
          ()
        case GetReceipts => receipts.values.toList.asInstanceOf[A]
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
