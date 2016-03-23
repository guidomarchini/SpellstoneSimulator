package simulator.model

/**
  * Created by guido on 22/03/16.
  */
object Utils {
  def diffToZero(x: Int, y: Int): Int = if (x > y) x-y else 0
  def min(x: Int, y: Int): Int = if(x>y) x else y

  def findAdjacents(cards: Seq[Card], index: Int): Seq[Card] = cards.slice(index-1, index+2)
}
