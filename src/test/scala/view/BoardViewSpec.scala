package view

import model.*
import org.scalatest.matchers.should.Matchers
import org.scalatest.wordspec.AnyWordSpec

class BoardViewSpec extends AnyWordSpec with Matchers:

  "BoardView" should {

    "print next player 1 when current player is one" in {
      val state = GameState(MillBoard(), Player(PlayerId.One), Player(PlayerId.Two), PlayerId.One)
      val view = BoardView()

      val out = new java.io.ByteArrayOutputStream()
      Console.withOut(out) {
        view.update(state)
      }

      out.toString should include("Next: Player 1")
    }

    "print next player 2 when current player is two" in {
      val state = GameState(MillBoard(), Player(PlayerId.One), Player(PlayerId.Two), PlayerId.Two)
      val view = BoardView()

      val out = new java.io.ByteArrayOutputStream()
      Console.withOut(out) {
        view.update(state)
      }

      out.toString should include("Next: Player 2")
    }

    "render board rows joined by system line separator" in {
      val board = MillBoard(3)
      val eol = sys.props("line.separator")

      BoardView().render(board) should be(board.rows.mkString(eol))
    }

    "render stones as player numbers" in {
      val board = MillBoard()
        .placeStone(Position(0, 0), PlayerId.One).get
        .placeStone(Position(0, 2), PlayerId.Two).get

      val eol = sys.props("line.separator")
      val rows = BoardView().render(board).split(eol).toSeq

      rows(0).charAt(0) should be('1')
      rows(0).charAt(30) should be('2')
    }

    "render stones with letter strategy" in {
      val board = MillBoard()
        .placeStone(Position(0, 0), PlayerId.One).get
        .placeStone(Position(0, 2), PlayerId.Two).get

      val eol = sys.props("line.separator")
      val rows = BoardView(LetterStoneSymbols).render(board).split(eol).toSeq

      rows(0).charAt(0) should be('X')
      rows(0).charAt(30) should be('O')
    }

    "render coordinate labels and footer" in {
      val board = MillBoard(3)
      val eol = sys.props("line.separator")
      val renderedRows = BoardView().renderWithCoords(board).split(eol).toSeq
      val expectedFooter = "  a    b    c    d    e    f    g"

      renderedRows(0).take(2) should be("1 ")
      renderedRows(1).take(2) should be("  ")
      renderedRows(2).take(2) should be("2 ")
      renderedRows.last should be(expectedFooter)
    }
  }