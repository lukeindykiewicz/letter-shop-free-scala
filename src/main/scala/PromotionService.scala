package lettershop.free

trait PromotionService {

  def threeForTwo(letters: String, p: Map[String, Price]): Double = {
    val promoLetter = Set('a', 'X')
    val threes: Seq[Char] => Int = _.grouped(3).toSeq.filter(_.size == 3).size
    letters.toSeq
      .groupBy(x => x)
      .filter { case (c, cs) => promoLetter.contains(c) }
      .map { case (c, cs) => c -> threes(cs) * p(c.toString).price }
      .values
      .sum
  }

  def countThreeForTwo(letters: String, prices: Map[String, Price]): Double => Double =
    _ - threeForTwo(letters, prices)

  val promo: Option[String] => Double =
    _.map(x => if (x == "10percent") 0.1 else 0.0).getOrElse(0.0)

  def countPromo(promoCode: Option[String]): Double => Double =
    _ * (1 - promo(promoCode))
}
