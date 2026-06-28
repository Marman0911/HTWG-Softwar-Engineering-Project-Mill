package model.game.impl

import model.board.BoardComponent
import model.board.Position
import model.game.GameState
import model.game.GameComponent
import model.player.PlayerComponent
import model.player.PlayerId
import org.scalatest.matchers.should.Matchers
import org.scalatest.wordspec.AnyWordSpec

class GameStateImplSpec extends AnyWordSpec with Matchers:

  "GameState.removeStone" should {

    "restore player one stone when undoing player one placement" in {
      val state = GameState()
      val pos = Position(0, 0)
      val afterPlace = state.placeStone(pos).get
      afterPlace.player1.stonesOnBoard should be(1)
      afterPlace.player1.stonesInHand should be(8)

      val afterRemove = afterPlace.removeStone(pos).get
      afterRemove.player1.stonesOnBoard should be(0)
      afterRemove.player1.stonesInHand should be(9)
      afterRemove.currentPlayer should be(PlayerId.One)
    }

    "restore player two stone when undoing player two placement" in {
      val state = GameState()
      val afterP1 = state.placeStone(Position(0, 0)).get
      val afterP2 = afterP1.placeStone(Position(0, 1)).get
      afterP2.player2.stonesOnBoard should be(1)

      val afterRemove = afterP2.removeStone(Position(0, 1)).get
      afterRemove.player2.stonesOnBoard should be(0)
      afterRemove.currentPlayer should be(PlayerId.Two)
    }

    "return None when removing from empty position" in {
      val state = GameState()
      state.removeStone(Position(0, 0)) should be(None)
    }

  }

  "GameState.moveStone" should {

    "move a stone to a neighbouring position" in {
      val state = GameState()
      val from = Position(0, 0)
      val to   = Position(0, 1)
      val afterPlace = state.placeStone(from).get
      // player2 setzt damit player1 wieder dran ist
      val afterP2 = afterPlace.placeStone(Position(1, 0)).get

      val afterMove = afterP2.moveStone(from, to)
      afterMove shouldBe defined
      afterMove.get.board.placedStones.get(to) should be(Some(PlayerId.One))
      afterMove.get.board.placedStones.get(from) should be(None)
      afterMove.get.currentPlayer should be(PlayerId.Two)
    }

    "return None when moving from a position not owned by current player" in {
      val state = GameState()
      val afterP1 = state.placeStone(Position(0, 0)).get
      // currentPlayer ist jetzt Two, aber Position(0,0) gehört One
      afterP1.moveStone(Position(0, 0), Position(0, 1)) should be(None)
    }

    "return None when target position is occupied" in {
      val state    = GameState()
      val afterP1  = state.placeStone(Position(0, 0)).get
      val afterP2  = afterP1.placeStone(Position(0, 1)).get
      // P1 versucht auf besetztes Feld zu ziehen
      afterP2.moveStone(Position(0, 0), Position(0, 1)) should be(None)
    }

    "return None when target is not a neighbour and player cannot fly" in {
      val state   = GameState()
      val afterP1 = state.placeStone(Position(0, 0)).get
      val afterP2 = afterP1.placeStone(Position(1, 0)).get
      // Position(0,0) → Position(2,0) sind keine Nachbarn
      afterP2.moveStone(Position(0, 0), Position(2, 0)) should be(None)
    }

  }

  "GameState.undoMoveStone" should {

    "restore the board to state before the move" in {
      val state    = GameState()
      val from     = Position(0, 0)
      val to       = Position(0, 1)
      val afterP1  = state.placeStone(from).get
      val afterP2  = afterP1.placeStone(Position(1, 0)).get
      val afterMove = afterP2.moveStone(from, to).get

      val afterUndo = afterMove.undoMoveStone(from, to).get
      afterUndo.board.placedStones.get(from) should be(Some(PlayerId.One))
      afterUndo.board.placedStones.get(to) should be(None)
      afterUndo.currentPlayer should be(PlayerId.One)
    }

  }