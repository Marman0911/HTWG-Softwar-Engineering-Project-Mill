package controller

import model.board.Position
import model.game.GameState
import org.scalatest.matchers.should.Matchers
import org.scalatest.wordspec.AnyWordSpec

class BoardViewMapperSpec extends AnyWordSpec with Matchers:

  "BoardViewMapper.boardRows" should {

    "produce 13 rows for the default board size 3" in {
      BoardViewMapper.boardRows(3).length should be(13)
    }

    "create the expected rows for size one" in {
      BoardViewMapper.boardRows(1) should be(
        Seq(
          "+----+----+",
          "|         |",
          "+         +",
          "|         |",
          "+----+----+"
        )
      )
    }

    "create the expected key rows for size three" in {
      val rows = BoardViewMapper.boardRows(3)

      rows(0) should be("+--------------+--------------+")
      rows(1) should be("|              |              |")
      rows(2) should be("|    +---------+---------+    |")
      rows(3) should be("|    |         |         |    |")
      rows(4) should be("|    |    +----+----+    |    |")
      rows(5) should be("|    |    |         |    |    |")
      rows(6) should be("+----+----+         +----+----+")
    }

    "build a mirrored shape around the middle row" in {
      val rows = BoardViewMapper.boardRows(3)
      rows should be(rows.reverse)
    }
  }

  "BoardViewMapper.toViewModel" should {

    "set nextPlayerNumber to 1 when current player is One" in {
      val state = GameState() // starts at PlayerId.One
      BoardViewMapper.toViewModel(state).nextPlayerNumber should be(1)
    }

    "set nextPlayerNumber to 2 when current player is Two" in {
      val state = GameState().placeStone(Position(0, 0)).get // switches to PlayerId.Two
      BoardViewMapper.toViewModel(state).nextPlayerNumber should be(2)
    }

    "produce no stones for an empty board" in {
      BoardViewMapper.toViewModel(GameState()).stones should be(empty)
    }

    "map a player One stone to playerNumber 1" in {
      val state = GameState().placeStone(Position(0, 0)).get
      val stones = BoardViewMapper.toViewModel(state).stones
      stones.find(s => s.row == 0 && s.col == 0).get.playerNumber should be(1)
    }

    "map a player Two stone to playerNumber 2" in {
      val state = GameState()
        .placeStone(Position(0, 0)).get // player 1
        .placeStone(Position(0, 1)).get // player 2
      val stones = BoardViewMapper.toViewModel(state).stones
      val (r2, c2) = state.board.posCoords(Position(0, 1))
      stones.find(s => s.row == r2 && s.col == c2).get.playerNumber should be(2)
    }

    "reflect the board size from the state" in {
      BoardViewMapper.toViewModel(GameState()).boardSize should be(3)
    }
  }
