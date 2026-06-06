package view

import controller.BoardViewModel
import controller.GameController
import controller.GameObserver
import controller.StonePlacement

// The View holds a reference to the Controller so it can actively pull
// the current state when the Controller sends its update signal.
class BoardView(controller: GameController, symbolStrategy: StoneSymbolStrategy = NumberStoneSymbols) extends GameObserver:
  private val eol = sys.props("line.separator")

  // The Controller calls this with no arguments – it is a pure signal.
  // The View pulls the DTO from the Controller (which fetched it from the Model).
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