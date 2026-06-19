package model.board

import model.board.BoardComponent
import model.board.Position
import model.player.PlayerId
import org.scalatest.matchers.should.Matchers
import org.scalatest.wordspec.AnyWordSpec

class MillBoardSpec extends AnyWordSpec with Matchers:

  "BoardComponent" should {

    "create the default board size" in {
      val board = BoardComponent.create()
      board.boardSize should be(3)
    }

    "initialize all positions as empty" in {
      val board = BoardComponent.create(3)
      board.placedStones shouldBe empty
      board.allPositions.length should be(24)
    }

    "map all slots to coordinates" in {
      val board = BoardComponent.create(3)
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
      val board = BoardComponent.create()
      val pos = Position(0, 0)

      val result = board.placeStone(pos, PlayerId.One)

      result shouldBe defined
      result.get.placedStones.get(pos) should be(Some(PlayerId.One))
      board.placedStones.get(pos) should be(None)
    }

    "reject placing on an occupied position" in {
      val board = BoardComponent.create()
      val pos = Position(1, 0)
      val afterFirst = board.placeStone(pos, PlayerId.One).get

      afterFirst.placeStone(pos, PlayerId.Two) should be(None)
    }
  }
