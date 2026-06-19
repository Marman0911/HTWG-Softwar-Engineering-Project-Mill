package model.board

import model.player.PlayerId

trait Board:

  def boardSize: Int

  def allPositions: Seq[Position]

  def posCoords(pos: Position): (Int, Int)

  def placedStones: Map[Position, PlayerId]

  def removeStone(pos: Position): Option[Board]

  def occupiedCount: Int =
    placedStones.size

  def placeStone(pos: Position, player: PlayerId): Option[Board]