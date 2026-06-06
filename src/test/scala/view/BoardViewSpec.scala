package view

import controller.BoardViewModel
import controller.StonePlacement
import model.MillBoard
import org.scalatest.matchers.should.Matchers
import org.scalatest.wordspec.AnyWordSpec

class BoardViewSpec extends AnyWordSpec with Matchers:

  // Helper: a GameState whose currentPlayer determines nextPlayerNumber in the view
  private def stateForPlayer(playerNumber: Int): controller.GameController =
    val ctrl = controller.GameController()
    if playerNumber == 2 then ctrl.handleInput("a1")
    ctrl

  private def emptyModel(boardSize: Int = 3, nextPlayerNumber: Int = 1): BoardViewModel =
    BoardViewModel(
      rows = controller.BoardViewMapper.boardRows(boardSize),
      boardSize = boardSize,
      stones = Seq.empty,
      nextPlayerNumber = nextPlayerNumber
    )

  "BoardView" should {

    "print next player 1 when current player is one" in {
      val ctrl = stateForPlayer(1)
      val view = BoardView(ctrl)

      val out = new java.io.ByteArrayOutputStream()
      Console.withOut(out) { view.update() }

      out.toString should include("Next: Player 1")
    }

    "print next player 2 when current player is two" in {
      val ctrl = stateForPlayer(2)
      val view = BoardView(ctrl)

      val out = new java.io.ByteArrayOutputStream()
      Console.withOut(out) { view.update() }

      out.toString should include("Next: Player 2")
    }

    "render board rows joined by system line separator" in {
      val viewModel = emptyModel(boardSize = 3)
      val eol = sys.props("line.separator")

      BoardView(controller.GameController()).render(viewModel) should be(viewModel.rows.mkString(eol))
    }

    "render stones as player numbers" in {
      val viewModel = emptyModel().copy(
        stones = Seq(
          StonePlacement(0, 0, 1),
          StonePlacement(0, 30, 2)
        )
      )

      val eol = sys.props("line.separator")
      val rows = BoardView(controller.GameController()).render(viewModel).split(eol).toSeq

      rows(0).charAt(0) should be('1')
      rows(0).charAt(30) should be('2')
    }

    "render stones with letter strategy" in {
      val viewModel = emptyModel().copy(
        stones = Seq(
          StonePlacement(0, 0, 1),
          StonePlacement(0, 30, 2)
        )
      )

      val eol = sys.props("line.separator")
      val rows = BoardView(controller.GameController(), LetterStoneSymbols).render(viewModel).split(eol).toSeq

      rows(0).charAt(0) should be('X')
      rows(0).charAt(30) should be('O')
    }

    "render coordinate labels and footer" in {
      val viewModel = emptyModel(boardSize = 3)
      val eol = sys.props("line.separator")
      val renderedRows = BoardView(controller.GameController()).renderWithCoords(viewModel).split(eol).toSeq
      val expectedFooter = "  a    b    c    d    e    f    g"

      renderedRows(0).take(2) should be("1 ")
      renderedRows(1).take(2) should be("  ")
      renderedRows(2).take(2) should be("2 ")
      renderedRows.last should be(expectedFooter)
    }
  }