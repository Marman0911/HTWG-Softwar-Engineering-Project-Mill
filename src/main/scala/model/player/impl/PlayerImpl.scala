package model.player.impl

import model.player.Player
import model.player.PlayerId

private[player] final class PlayerImpl(
    val id: PlayerId,
    val stonesInHand: Int,
    val stonesOnBoard: Int
) extends Player:

  def totalStones: Int =
    stonesInHand + stonesOnBoard

  def hasLost: Boolean =
    totalStones < 3

  def canFly: Boolean =
    stonesOnBoard == 3 && stonesInHand == 0