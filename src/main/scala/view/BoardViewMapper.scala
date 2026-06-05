package view

import model.GameState
import model.PlayerId

object BoardViewMapper:
  def toViewModel(state: GameState): BoardViewModel =
    val stones = state.board.stones.collect:
      case (pos, Some(player)) =>
        val (row, col) = state.board.posCoords(pos)
        val playerNumber = if player == PlayerId.One then 1 else 2
        StonePlacement(row, col, playerNumber)

    BoardViewModel(
      rows = state.board.rows,
      boardSize = state.board.boardSize,
      stones = stones.toSeq,
      nextPlayerNumber = if state.currentPlayer == PlayerId.One then 1 else 2
    )
