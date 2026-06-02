package model

import org.scalatest.matchers.should.Matchers
import org.scalatest.wordspec.AnyWordSpec

class GameStateSpec extends AnyWordSpec with Matchers:

  "GameState" should {

    "start with default players and current player one" in {
      val state = GameState()

      state.currentPlayer should be(PlayerId.One)
      state.player1.id should be(PlayerId.One)
      state.player2.id should be(PlayerId.Two)
      state.currentPlayerObj should be(state.player1)
    }

    "place a stone and switch turns" in {
      val state = GameState()
      val pos = Position(0, 0)

      val next = state.placeStone(pos)

      next shouldBe defined
      next.get.board.stones(pos) should be(Some(PlayerId.One))
      next.get.currentPlayer should be(PlayerId.Two)
      next.get.currentPlayerObj should be(next.get.player2)
    }

    "reject placing on occupied positions" in {
      val state = GameState()
      val pos = Position(0, 0)

      val first = state.placeStone(pos).get
      val second = first.placeStone(pos)

      second should be(None)
    }
  }

  "MoveType" should {
    "contain all game phases currently implemented" in {
      MoveType.values.toSet should be(Set(MoveType.Place, MoveType.Move, MoveType.Fly, MoveType.Remove))
    }
  }
