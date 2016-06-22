package lettershop.free

trait Programs
    extends StorageConstructors {

  def getCartProgram(cartId: String): Storage[Cart] =
    for {
     cart <- getCart(cartId) 
    } yield cart

  def addToCartProgram(cartId: String, letters: String): Storage[Unit] =
    for {
      _ <- addToCart(cartId, letters)
    } yield ()
  
  def updateCartProgram(cartId: String, letters: String): Storage[Unit] =
    for {
      old <- getCart(cartId)
      _ <- addToCart(cartId, old.letters + letters)
    } yield ()

}

trait Compilers {

  import cats.{Id, ~>}

  def storageCompiler: StorageADT ~> Id =
    new (StorageADT ~> Id) {

      import scala.collection.concurrent.TrieMap
      var carts: TrieMap[String, String] = TrieMap.empty
      var prices: TrieMap[String, Double] = TrieMap.empty
      var receipts: TrieMap[String, ReceiptHistory] = TrieMap.empty

      def apply[A](fa: StorageADT[A]): Id[A] = fa match {
        case GetCart(cartId) => Cart(carts.getOrElse(cartId, ""))
        case AddToCart(cartId, letters) =>
          carts += (cartId -> letters)
          ()
        case _ =>
          println("Not implemented yet")
          "not implemented".asInstanceOf[A]
      }

    }

}
