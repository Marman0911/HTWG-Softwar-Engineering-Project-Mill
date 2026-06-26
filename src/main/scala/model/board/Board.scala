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

  // Verschiebt einen Stein nur dann, wenn der Startstein dem Spieler gehört
  // und das Zielfeld frei ist. Die Nachbar-Regel prüft GameState.
  def moveStone(from: Position, to: Position, player: PlayerId): Option[Board]

  // Liefert die direkt verbundenen Positionen auf dem Mühlebrett.
  def neighbours(pos: Position): Seq[Position]
