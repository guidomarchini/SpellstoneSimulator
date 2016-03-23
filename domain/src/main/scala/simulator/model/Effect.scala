package simulator.model

/**
  * Created by guido on 22/03/16.
  */
class Effect {

}


class Empowered(val value: Int) extends Effect
class Barriered(val value: Int) extends Effect
class Hexed(val value: Int) extends Effect
class Weakened(val value: Int) extends Effect
class Scorched(val value: Int) extends Effect
class Poisoned(val value: Int) extends Effect
class Frozen extends Effect
class Berserked(val value: Int) extends Effect
class Cooldown(val value: Int) extends Effect