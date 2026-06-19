package model.player

trait Player:

  def id: PlayerId

  def stonesInHand: Int

  def stonesOnBoard: Int

  def totalStones: Int

  def hasLost: Boolean

  def canFly: Boolean