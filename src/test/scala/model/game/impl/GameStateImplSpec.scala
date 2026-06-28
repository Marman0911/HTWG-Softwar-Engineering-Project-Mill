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

    "GameState.previousPlayer via undoMoveStone" should {

        "return PlayerId.One as previousPlayer when currentPlayer is Two" in {
            val state   = GameState()
            val from    = Position(0, 0)
            val to      = Position(0, 1)
            val afterP1 = state.placeStone(from).get
            val afterP2 = afterP1.placeStone(Position(1, 0)).get
            // nach moveStone ist currentPlayer = Two
            val afterMove = afterP2.moveStone(from, to).get
            afterMove.currentPlayer should be(PlayerId.Two)
            // undoMoveStone nutzt previousPlayer — wenn current=Two dann previous=One
            val afterUndo = afterMove.undoMoveStone(from, to).get
            afterUndo.currentPlayer should be(PlayerId.One)
        }

        "return PlayerId.Two as previousPlayer when currentPlayer is One" in {
            val state    = GameState()
            val from     = Position(0, 1)
            val to       = Position(0, 0)
            val afterP1  = state.placeStone(Position(0, 2)).get
            val afterP2  = afterP1.placeStone(from).get
            // P1 ist dran, P2-Stein auf from — wir brauchen einen P2-Stein zum Bewegen
            // Mache einen weiteren Zug damit P2 dran ist und dann undo
            val afterP1b = afterP2.placeStone(Position(1, 0)).get
            val afterP2b = afterP1b.placeStone(Position(1, 2)).get
            // Nun P1 bewegen damit currentPlayer danach Two ist
            val afterMove = afterP2b.moveStone(Position(0, 2), Position(0, 3))
            afterMove shouldBe defined
            afterMove.get.currentPlayer should be(PlayerId.Two)
            val afterUndo = afterMove.get.undoMoveStone(Position(0, 2), Position(0, 3)).get
            afterUndo.currentPlayer should be(PlayerId.One)
        }

        }

        "GameState.moveStone with canFly" should {

        "allow flying when player has exactly 3 stones and none in hand" in {
            val flyingP1 = PlayerComponent.create(PlayerId.One, stonesInHand = 0, stonesOnBoard = 3)
            val normalP2 = PlayerComponent.create(PlayerId.Two, stonesInHand = 0, stonesOnBoard = 3)
            var b = BoardComponent.create()
            b = b.placeStone(Position(0, 0), PlayerId.One).get
            b = b.placeStone(Position(0, 2), PlayerId.One).get
            b = b.placeStone(Position(0, 4), PlayerId.One).get
            b = b.placeStone(Position(1, 0), PlayerId.Two).get
            b = b.placeStone(Position(1, 2), PlayerId.Two).get
            b = b.placeStone(Position(1, 4), PlayerId.Two).get

            val flyState = GameComponent.create(b, flyingP1, normalP2, PlayerId.One)
            flyingP1.canFly should be(true)

            val result = flyState.moveStone(Position(0, 0), Position(2, 0))
            result shouldBe defined
            result.get.board.placedStones.get(Position(2, 0)) should be(Some(PlayerId.One))
        }

    }
}