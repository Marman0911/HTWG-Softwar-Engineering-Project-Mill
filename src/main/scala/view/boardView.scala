package view

import controller.BoardViewModel
import controller.IController
import controller.GameObserver
import controller.StonePlacement

class BoardView(controller: IController, symbolStrategy: StoneSymbolStrategy = NumberStoneSymbols) extends GameObserver:
  private val eol = sys.props("line.separator")

  def update(): Unit =
    val viewModel = controller.boardViewModel
    println(renderWithCoords(viewModel))
    println(s"Next: Player ${viewModel.nextPlayerNumber}")

  private def stoneRows(viewModel: BoardViewModel): Seq[String] =
    val rowsArr = viewModel.rows.map(_.toCharArray)
    viewModel.stones.foreach: stone =>
      rowsArr(stone.row)(stone.col) = symbolStrategy.symbol(stone.playerNumber)
    rowsArr.map(_.mkString)

  def render(viewModel: BoardViewModel): String =
    stoneRows(viewModel).mkString(eol)

  def renderWithCoords(viewModel: BoardViewModel): String =
    val labeled = stoneRows(viewModel).zipWithIndex.map: (row, i) =>
      val label = if i % 2 == 0 then s"${i / 2 + 1} " else "  "
      label + row

    val footer = "  " + (0 to 2 * viewModel.boardSize)
      .map(i => ('a' + i).toChar).mkString("    ")

    (labeled :+ footer).mkString(eol)