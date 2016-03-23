package simulator.model

/**
  * Created by guido on 22/03/16.
  */
class Damage (val value: Int)
class PierceDamage(override val value: Int, val piercing: Int) extends Damage(value)

trait PhysicalDamage
trait IndirectDamage
trait StatusDamage
trait VengeanceDamage

object Damage {
  def physicalDamage(value: Int)  = new Damage(value) with PhysicalDamage
  def indirectDamage(value: Int)  = new Damage(value) with IndirectDamage
  def statusDamage(value: Int)    = new Damage(value) with StatusDamage
  def vengeanceDamage(value: Int) = new Damage(value) with VengeanceDamage
  def piercingDamage(value: Int, piercing: Int) = new PierceDamage(value, piercing)
}