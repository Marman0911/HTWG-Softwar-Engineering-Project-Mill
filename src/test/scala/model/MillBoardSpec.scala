package model

import org.scalatest.matchers.should.Matchers
import org.scalatest.wordspec.AnyWordSpec

class MillBoardSpec extends AnyWordSpec with Matchers:

  "MillBoard" should {

    "create the default board size" in {
      val board = MillBoard()
      board.boardSize should be(3)
      board.rows.length should be(13)
    }

    "create the expected minimal board for size one" in {
      val board = MillBoard(1)

      board.rows should be(
        Seq(
          "+----+----+",
          "|         |",
          "+         +",
          "|         |",
          "+----+----+"
        )
      )
    }

    "build a mirrored shape around the middle row" in {
      val board = MillBoard(3)
      board.rows should be(board.rows.reverse)
    }

    "initialize all positions as empty" in {
      val board = MillBoard(3)
      board.stones.values.forall(_.isEmpty) should be(true)
      board.allPositions.length should be(24)
    }

    "map all slots to coordinates" in {
      val board = MillBoard(3)
      val ring = 1

      board.posCoords(Position(ring, 0)) should be((2, 5))
      board.posCoords(Position(ring, 1)) should be((2, 15))
      board.posCoords(Position(ring, 2)) should be((2, 25))
      board.posCoords(Position(ring, 3)) should be((6, 25))
      board.posCoords(Position(ring, 4)) should be((10, 25))
      board.posCoords(Position(ring, 5)) should be((10, 15))
      board.posCoords(Position(ring, 6)) should be((10, 5))
      board.posCoords(Position(ring, 7)) should be((6, 5))
    }

    "place a stone on an empty position" in {
      val board = MillBoard()
      val pos = Position(0, 0)

      val result = board.placeStone(pos, PlayerId.One)

      result shouldBe defined
      result.get.stones(pos) should be(Some(PlayerId.One))
      board.stones(pos) should be(None)
    }

    "reject placing on an occupied position" in {
      val board = MillBoard()
      val pos = Position(1, 0)
      val afterFirst = board.placeStone(pos, PlayerId.One).get

      afterFirst.placeStone(pos, PlayerId.Two) should be(None)
    }
  }
