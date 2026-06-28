package model.board.impl

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

    "count occupied positions with occupiedCount" in {
      val board = BoardComponent.create()
      board.occupiedCount should be(0)

      val pos1 = Position(0, 0)
      val pos2 = Position(0, 1)
      val after1 = board.placeStone(pos1, PlayerId.One).get
      after1.occupiedCount should be(1)

      val after2 = after1.placeStone(pos2, PlayerId.Two).get
      after2.occupiedCount should be(2)
      }

    "decrease occupiedCount after removing a stone" in {
      val board = BoardComponent.create()
      val pos = Position(0, 0)
      val afterPlace = board.placeStone(pos, PlayerId.One).get
      afterPlace.occupiedCount should be(1)

      val afterRemove = afterPlace.removeStone(pos).get
      afterRemove.occupiedCount should be(0)
      }

    "move a stone to a free position" in {
      val board = BoardComponent.create()
      val from = Position(0, 0)
      val to   = Position(0, 1)
      val afterPlace = board.placeStone(from, PlayerId.One).get
      val result = afterPlace.moveStone(from, to, PlayerId.One)
      result shouldBe defined
      result.get.placedStones.get(to) should be(Some(PlayerId.One))
      result.get.placedStones.get(from) should be(None)
    }

    "return None when moving a stone that does not belong to the player" in {
      val board = BoardComponent.create()
      val from = Position(0, 0)
      val to   = Position(0, 1)
      val afterPlace = board.placeStone(from, PlayerId.One).get
      afterPlace.moveStone(from, to, PlayerId.Two) should be(None)
    }

    "return None when moving to an occupied position" in {
      val board = BoardComponent.create()
      val from = Position(0, 0)
      val to   = Position(0, 1)
      val after1 = board.placeStone(from, PlayerId.One).get
      val after2 = after1.placeStone(to, PlayerId.Two).get
      after2.moveStone(from, to, PlayerId.One) should be(None)
    }

    "return neighbours for a valid position" in {
      val board = BoardComponent.create()
      val pos = Position(0, 1)
      val neighbours = board.neighbours(pos)
      neighbours should not be empty
      neighbours should contain(Position(0, 0))
      neighbours should contain(Position(0, 2))
    }

    "return inner ring neighbour for odd slot with ring > 0" in {
      val board = BoardComponent.create()
      val pos = Position(1, 1)
      val neighbours = board.neighbours(pos)
      neighbours should contain(Position(0, 1))
    }

    "return outer ring neighbour for odd slot with ring < boardSize - 1" in {
      val board = BoardComponent.create()
      val pos = Position(1, 1)
      val neighbours = board.neighbours(pos)
      neighbours should contain(Position(2, 1))
    }

    "return empty neighbours for invalid position" in {
      val board = BoardComponent.create()
      board.neighbours(Position(99, 99)) should be(empty)
    }

    "return None when removing from empty position" in {
      val board = BoardComponent.create()
      board.removeStone(Position(0, 0)) should be(None)
    }
  }
