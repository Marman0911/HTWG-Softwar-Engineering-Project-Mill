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

  def removeStone(pos: Position): Option[Board] =
    if stones.getOrElse(pos, None).isEmpty then None
    else Some(copy(stones = stones.updated(pos, None)))

  def moveStone(from: Position, to: Position, player: PlayerId): Option[Board] =
    val startBelongsToPlayer =
      stones.getOrElse(from, None).contains(player)

    val targetIsFree =
      stones.getOrElse(to, None).isEmpty

    if !startBelongsToPlayer || !targetIsFree then None
    else
      Some(
        copy(
          stones = stones
            .updated(from, None)
            .updated(to, Some(player))
        )
      )

  def neighbours(pos: Position): Seq[Position] =
    if !allPositions.contains(pos) then Seq.empty
    else
      val previousOnRing =
        Position(pos.ring, (pos.slot + 7) % 8)

      val nextOnRing =
        Position(pos.ring, (pos.slot + 1) % 8)

      val innerRingNeighbour =
        if pos.slot % 2 == 1 && pos.ring > 0 then
          Seq(Position(pos.ring - 1, pos.slot))
        else
          Seq.empty

      val outerRingNeighbour =
        if pos.slot % 2 == 1 && pos.ring < boardSize - 1 then
          Seq(Position(pos.ring + 1, pos.slot))
        else
          Seq.empty

      Seq(previousOnRing, nextOnRing) ++
        innerRingNeighbour ++
        outerRingNeighbour
