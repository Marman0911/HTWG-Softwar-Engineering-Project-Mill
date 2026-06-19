package model.board.impl

import model.board.Board
import model.board.Position
import model.player.PlayerId

private[board] object MillBoard:

  private def emptyStones(boardSize: Int): Map[Position, Option[PlayerId]] =
    (for
      r <- 0 until boardSize
      s <- 0 until 8
    yield Position(r, s) -> None).toMap

  def apply(boardSize: Int = 3): Board =
    new MillBoard(boardSize, emptyStones(boardSize))


private[board] case class MillBoard private (
    boardSize: Int,
    stones: Map[Position, Option[PlayerId]]
) extends Board:

  val allPositions: Seq[Position] =
    for
      r <- 0 until boardSize
      s <- 0 until 8
    yield Position(r, s)

  def posCoords(pos: Position): (Int, Int) =
    val n = boardSize
    val r = pos.ring

    pos.slot match
      case 0 => (r * 2, 5 * r)
      case 1 => (r * 2, 5 * n)
      case 2 => (r * 2, 10 * n - 5 * r)
      case 3 => (2 * n, 10 * n - 5 * r)
      case 4 => (4 * n - r * 2, 10 * n - 5 * r)
      case 5 => (4 * n - r * 2, 5 * n)
      case 6 => (4 * n - r * 2, 5 * r)
      case 7 => (2 * n, 5 * r)

  def placedStones: Map[Position, PlayerId] =
    stones.collect:
      case (pos, Some(player)) => pos -> player

  def placeStone(pos: Position, player: PlayerId): Option[Board] =
    if stones.getOrElse(pos, None).isDefined then None
    else Some(copy(stones = stones.updated(pos, Some(player))))