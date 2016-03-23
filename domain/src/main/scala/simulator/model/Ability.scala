package simulator.model

import Utils._

/**
  * Created by guido on 22/03/16.
  */
abstract class Ability (val value: Int) {
  var modifier: Int = 0
  def getValue = this.value + this.modifier
  def startOfTurn(card: Card): Unit = this.modifier = 0
}

class Invisibility(value: Int) extends Ability(value) {
  def onReceiveDamage (context: Context): Unit = context.damage match {
    case damage: IndirectDamage if (this.getValue > 0) => {
      this.modifier -= 1
      context.damage = Damage.indirectDamage(0);
    }
  }
}

class Armor(value: Int) extends Ability(value) {
  def onReceiveDamage (context: Context): Unit = context.damage match {
    case damage: PhysicalDamage => {
      context.damage = Damage.physicalDamage(diffToZero(damage.value, this.getValue))
    }
    case damage: PierceDamage => {
      val piercedValue = diffToZero(this.modifier, damage.piercing)
      this.modifier -= piercedValue
      context.damage = Damage.piercingDamage(diffToZero(damage.value, this.getValue), damage.piercing - piercedValue)
    }
  }
}

class Vengeance(value: Int) extends Ability(value) {
  def onReceiveDamage (context: Context): Unit = context.damage match {
    case damage: PhysicalDamage => context.vengeanceDamage = Damage.vengeanceDamage(this.getValue)
  }
}

class Empower(value: Int, val strategy: TargetStrategy) extends Ability(value) {
  def onBeforeCombat (context: Context): Unit = this.strategy.apply(context.activePlayer.table, empower, this.getValue)
  def empower (card: Card, value: Int): Unit = card.addEffect(new Empowered(this.getValue))
}

class Heal(value: Int, val strategy: TargetStrategy) extends Ability(value) {
  def onActivation (context: Context): Unit = this.strategy.apply(context.activePlayer.table, heal, this.getValue)
  def heal (card: Card, value: Int): Unit = card.damage = diffToZero(card.damage, this.getValue)
}

class Barrier(value: Int, val strategy: TargetStrategy) extends Ability(value) {
  def onActivation (context: Context): Unit = this.strategy.apply(context.activePlayer.table, barrier, this.getValue)
  def barrier (card: Card, value: Int): Unit = card.addEffect(new Barriered(this.getValue))
}

class Legion(value: Int, val strategy: MatchStrategy) extends Ability(value) {
  def onBeforeCombat (context: Context): Unit = {
    val adjacentCards = findAdjacents(context.activePlayer.table, context.activePlayer.table.indexOf(context.activeCard))
    adjacentCards.foreach((card: Card) => if(card != context.activeCard && strategy.applies(card)) empower(card, this.getValue))
  }

  def empower (card: Card, value: Int): Unit = card.addEffect(new Empowered(this.getValue))
}

class Fervor(value: Int, val strategy: MatchStrategy) extends Ability(value) {
  def onBeforeCombat(context: Context): Unit = {
    val adjacentCards = findAdjacents(context.activePlayer.table, context.activePlayer.table.indexOf(context.activeCard))
    adjacentCards.foreach((card: Card) => if (card != context.activeCard && strategy.applies(card)) empower(context.activeCard, this.getValue))
  }

  def empower(card: Card, value: Int): Unit = card.addEffect(new Empowered(this.getValue))
}

class FrostBreath(value: Int) extends Ability(value) {
  def onActivation(context: Context): Unit = {
    val adjacentCards = findAdjacents(context.defendingPlayer.table, context.activePlayer.table.indexOf(context.activeCard))
    adjacentCards.foreach((card: Card) => card.receiveDamage(Damage.indirectDamage(this.getValue)));
  }
}

class Hex(value: Int, val strategy: TargetStrategy) extends Ability(value) {
  def onActivation (context: Context): Unit = this.strategy.apply(context.defendingPlayer.table, hex, this.getValue)
  def hex(card: Card, value: Int): Unit = card.addEffect(new Hexed(this.getValue))
}

class Bolt(value: Int, val strategy: TargetStrategy) extends Ability(value) {
  def onActivation (context: Context): Unit = this.strategy.apply(context.defendingPlayer.table, bolt, this.getValue)
  def bolt(card: Card, value: Int): Unit = card.receiveDamage(Damage.indirectDamage(this.getValue))
}

class Freeze(value: Int, val strategy: TargetStrategy) extends Ability(value) {
  def onActivation (context: Context): Unit = if(this.modifier % this.value == 0) this.strategy.apply(context.defendingPlayer.table, freeze, null)
  def freeze(card: Card, value: Int): Unit = card.addEffect(new Frozen())

  override def startOfTurn(card: Card): Unit = if (card.isActive()) this.modifier += 1
}

class Weaken(value: Int, val strategy: TargetStrategy) extends Ability(value) {
  def onActivation (context: Context): Unit = this.strategy.apply(context.defendingPlayer.table, weaken, this.getValue)
  def weaken(card: Card, value: Int): Unit = card.addEffect(new Weakened(this.getValue))
}

class Pierce(value: Int) extends Ability(value) {
  def onActivation(context: Context): Unit = context.damage = Damage.piercingDamage(context.damage.value, this.getValue)
}

class Scorch(value: Int) extends Ability(value) {
  def afterApplyDamage(context: Context): Unit = context.targetCard.addEffect(new Scorched(this.getValue))
}

class Poison(value: Int) extends Ability(value) {
  def afterApplyDamage(context: Context): Unit = if (context.damage.value > 0) context.targetCard.addEffect(new Poisoned(this.getValue))
}

class Syphon(value: Int) extends Ability(value) {
  def afterApplyDamage(context: Context): Unit = if (context.damage.value > 0) heal(context.activeCard, min(context.damage.value, this.getValue))
  def heal (card: Card, value: Int): Unit = card.damage = diffToZero(card.damage, this.getValue)
}

class Berserk(value: Int) extends Ability(value) {
  def afterApplyDamage(context: Context): Unit = if (context.damage.value > 0) context.activeCard.addEffect(new Berserked(this.getValue))
}

class DoubleStrike(value: Int) extends Ability(value) {
  def onActivation (context: Context): Unit = if(this.modifier % this.value == 0) context.doubleStrike = true
  def afterApplyDamage(context: Context): Unit = if(this.modifier % this.value == 0) context.doubleStrike = true

  override def startOfTurn(card: Card): Unit = if (card.isActive()) this.modifier += 1
}