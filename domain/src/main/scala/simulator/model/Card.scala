package simulator.model

/**
  * Created by guido on 22/03/16.
  */
class Card (val attack: Int, val life: Int, delay: Int) {
  var damage: Int = 0
  var effects: Seq[Effect] = Seq(new Cooldown(delay))

  def receiveDamage(value: Int): Unit = this.damage += value
  def addEffect(effect: Effect): Unit = this.effects = this.effects :+ effect

  def isActive(): Boolean = this.damage != this.life && !this.effects.exists((e: Effect) => e match {
    case effect: Cooldown => true
    case effect: Freeze => true
    case _ => false
  })

  def isValidTarget(): Boolean = this.damage != this.life && !this.effects.exists((e: Effect) => e match {
    case effect: Cooldown => effect.value > 1
    case _ => false
  })

}
