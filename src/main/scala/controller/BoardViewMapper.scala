package controller

import model.GameState
import model.PlayerId

object BoardViewMapper:

  private val intersection = "+"
  private val step         = "-"
  private val hPath        = step * 4
  private val vPath        = "|"
  private val nml          = " " * 4

  private def crossroad(n: Int): String =
    intersection + hPath * n + step * (n - 1)

  private def horizRow(boardSize: Int, k: Int): String =
    (vPath + nml) * k +
      crossroad(boardSize - k) * 2 + intersection +
      (nml + vPath) * k

  private def vertRowOuter(boardSize: Int, k: Int): String =
    val inner = " " * (5 * (boardSize - k) - 1)
    (vPath + nml) * k + vPath + inner + vPath + inner + vPath + (nml + vPath) * k

  private def vertRowInner(boardSize: Int): String =
    val gap = " " * 9
    (vPath + nml) * (boardSize - 1) + vPath + gap + vPath + (nml + vPath) * (boardSize - 1)

  private def middleRow(boardSize: Int): String =
    intersection + (hPath + intersection) * (boardSize - 1) +
      " " * 9 +
      (intersection + hPath) * (boardSize - 1) + intersection

  def boardRows(boardSize: Int): Seq[String] =
    val topHalf: Seq[String] =
      (0 until boardSize).flatMap: k =>
        val h = horizRow(boardSize, k)
        val v = if k < boardSize - 1 then vertRowOuter(boardSize, k) else vertRowInner(boardSize)
        Seq(h, v)
    topHalf ++ Seq(middleRow(boardSize)) ++ topHalf.reverse

  def toViewModel(state: GameState): BoardViewModel =
    val stones = state.board.stones.collect:
      case (pos, Some(player)) =>
        val (row, col) = state.board.posCoords(pos)
        val playerNumber = if player == PlayerId.One then 1 else 2
        StonePlacement(row, col, playerNumber)

    BoardViewModel(
      rows = boardRows(state.board.boardSize),
      boardSize = state.board.boardSize,
      stones = stones.toSeq,
      nextPlayerNumber = if state.currentPlayer == PlayerId.One then 1 else 2
    )
