package model.game.impl

import model.board.{BoardComponent, Position}
import model.game.{GameComponent, GameState}
import model.player.{PlayerComponent, PlayerId}
import org.scalatest.matchers.should.Matchers
import org.scalatest.wordspec.AnyWordSpec

class GameStateImplAdvancedSpec extends AnyWordSpec with Matchers:

  private def stateWith(
      stones: Seq[(Position, PlayerId)],
      playerOneStonesInHand: Int = 0,
      playerTwoStonesInHand: Int = 0,
      currentPlayer: PlayerId = PlayerId.One
  ): GameState =
    val board =
      stones.foldLeft(BoardComponent.create(3)): (currentBoard, stone) =>
        currentBoard.placeStone(stone._1, stone._2).get

    val playerOneOnBoard =
      stones.count(_._2 == PlayerId.One)

    val playerTwoOnBoard =
      stones.count(_._2 == PlayerId.Two)

    GameComponent.create(
      board,
      PlayerComponent.create(
        PlayerId.One,
        stonesInHand = playerOneStonesInHand,
        stonesOnBoard = playerOneOnBoard
      ),
      PlayerComponent.create(
        PlayerId.Two,
        stonesInHand = playerTwoStonesInHand,
        stonesOnBoard = playerTwoOnBoard
      ),
      currentPlayer
    )

  "GameState.placeStone" should {

    "not allow placing when the current player has no stones in hand" in {
      val state =
        stateWith(
          stones = Seq.empty,
          playerOneStonesInHand = 0,
          playerTwoStonesInHand = 0
        )

      state.placeStone(Position(0, 0)) shouldBe None
    }

    "keep player two on turn when player two completes a mill" in {
      val state =
        stateWith(
          stones = Seq(
            Position(0, 0) -> PlayerId.Two,
            Position(0, 1) -> PlayerId.Two,
            Position(1, 0) -> PlayerId.One
          ),
          playerOneStonesInHand = 8,
          playerTwoStonesInHand = 7,
          currentPlayer = PlayerId.Two
        )

      val afterMill =
        state.placeStone(Position(0, 2)).get

      afterMill.board.placedStones.get(Position(0, 2)) shouldBe Some(PlayerId.Two)
      afterMill.player2.stonesInHand shouldBe 6
      afterMill.player2.stonesOnBoard shouldBe 3
      afterMill.currentPlayer shouldBe PlayerId.Two
    }
  }

  "GameState.removeOpponentStone" should {

    "remove player one's stone when player two formed the mill" in {
      val target =
        Position(0, 0)

      val state =
        stateWith(
          stones = Seq(
            target -> PlayerId.One,
            Position(1, 0) -> PlayerId.Two
          ),
          currentPlayer = PlayerId.Two
        )

      val afterRemoval =
        state.removeOpponentStone(target).get

      afterRemoval.board.placedStones.get(target) shouldBe None
      afterRemoval.player1.stonesOnBoard shouldBe 0
      afterRemoval.player1.stonesInHand shouldBe 0
      afterRemoval.currentPlayer shouldBe PlayerId.One
    }

    "reject removing a stone that does not belong to the opponent" in {
      val state =
        stateWith(
          stones = Seq(
            Position(0, 0) -> PlayerId.One,
            Position(1, 0) -> PlayerId.Two
          ),
          currentPlayer = PlayerId.One
        )

      state.removeOpponentStone(Position(0, 0)) shouldBe None
      state.removeOpponentStone(Position(2, 0)) shouldBe None
    }
  }

  "GameState.restoreOpponentStone" should {

    "restore player one's stone and return the turn to player two" in {
      val target =
        Position(0, 0)

      val state =
        stateWith(
          stones = Seq(
            target -> PlayerId.One,
            Position(1, 0) -> PlayerId.Two
          ),
          currentPlayer = PlayerId.Two
        )

      val afterRemoval =
        state.removeOpponentStone(target).get

      val restored =
        afterRemoval.restoreOpponentStone(target).get

      restored.board.placedStones.get(target) shouldBe Some(PlayerId.One)
      restored.player1.stonesOnBoard shouldBe 1
      restored.currentPlayer shouldBe PlayerId.Two
    }
  }

  "GameState.moveStone" should {

    "keep the current player on turn after a move completes a mill" in {
      val state =
        stateWith(
          stones = Seq(
            Position(0, 0) -> PlayerId.One,
            Position(0, 1) -> PlayerId.One,
            Position(0, 3) -> PlayerId.One,
            Position(1, 0) -> PlayerId.Two,
            Position(1, 1) -> PlayerId.Two,
            Position(1, 2) -> PlayerId.Two
          ),
          currentPlayer = PlayerId.One
        )

      val afterMill =
        state.moveStone(Position(0, 3), Position(0, 2)).get

      afterMill.board.placedStones.get(Position(0, 2)) shouldBe Some(PlayerId.One)
      afterMill.currentPlayer shouldBe PlayerId.One
    }

    "switch from player two to player one after a move without a mill" in {
      val state =
        stateWith(
          stones = Seq(
            Position(0, 0) -> PlayerId.Two,
            Position(1, 0) -> PlayerId.One
          ),
          currentPlayer = PlayerId.Two
        )

      val afterMove =
        state.moveStone(Position(0, 0), Position(0, 1)).get

      afterMove.currentPlayer shouldBe PlayerId.One
    }
  }
