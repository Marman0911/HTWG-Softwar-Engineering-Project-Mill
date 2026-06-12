package model

trait GameVariant:
  def boardSize: Int
  def stonesPerPlayer: Int
  def name: String

object StandardMill extends GameVariant:
  val boardSize       = 3
  val stonesPerPlayer = 9
  val name            = "Nine Men's Morris"

object SmallMill extends GameVariant:
  val boardSize       = 2
  val stonesPerPlayer = 6
  val name            = "Six Men's Morris"