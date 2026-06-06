package controller

case class StonePlacement(row: Int, col: Int, playerNumber: Int)

case class BoardViewModel(
  rows: Seq[String],
  boardSize: Int,
  stones: Seq[StonePlacement],
  nextPlayerNumber: Int
)
