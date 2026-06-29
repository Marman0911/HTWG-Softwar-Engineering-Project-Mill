package controller.command

import model.board.{Board, BoardComponent, Position}
import model.game.{GameComponent, GameState}
import model.player.{PlayerComponent, PlayerId}
import org.scalatest.matchers.should.Matchers
import org.scalatest.wordspec.AnyWordSpec

class RemoveCommandSpec extends AnyWordSpec with Matchers:

  private def stateWith(
      stones: Seq[(Position, PlayerId)]
  ): GameState =
    val board: Board =
      stones.foldLeft(BoardComponent.create(3)): (currentBoard, stone) =>
        currentBoard.placeStone(stone._1, stone._2).get

    val playerOneOnBoard =
      stones.count(_._2 == PlayerId.One)

    val playerTwoOnBoard =
      stones.count(_._2 == PlayerId.Two)

    GameComponent.create(
      board,
      PlayerComponent.create(PlayerId.One, stonesInHand = 0, stonesOnBoard = playerOneOnBoard),
      PlayerComponent.create(PlayerId.Two, stonesInHand = 0, stonesOnBoard = playerTwoOnBoard),
      PlayerId.One
    )

  "RemoveCommand.execute" should {

    "remove an allowed opponent stone without returning it to the reserve" in {
      val target =
        Position(1, 0)

      val state =
        stateWith(
          Seq(
            Position(0, 0) -> PlayerId.One,
            Position(0, 1) -> PlayerId.One,
            Position(0, 2) -> PlayerId.One,
            target -> PlayerId.Two,
            Position(1, 1) -> PlayerId.Two,
            Position(1, 2) -> PlayerId.Two
          )
        )

      val result =
        RemoveCommand(target).execute(state)

      result.isSuccess shouldBe true
      result.get.board.placedStones.get(target) shouldBe None
      result.get.player2.stonesOnBoard shouldBe 2
      result.get.player2.stonesInHand shouldBe 0
      result.get.currentPlayer shouldBe PlayerId.Two
    }

    "reject an own stone" in {
      val ownStone =
        Position(0, 0)

      val state =
        stateWith(
          Seq(
            ownStone -> PlayerId.One,
            Position(1, 0) -> PlayerId.Two
          )
        )

      val result =
        RemoveCommand(ownStone).execute(state)

      result.isFailure shouldBe true
      result.failed.get.getMessage shouldBe
        "Invalid removal. Choose a removable opponent stone."
    }

    "reject an opponent stone inside a mill when another opponent stone is outside" in {
      val protectedStone =
        Position(0, 0)

      val state =
        stateWith(
          Seq(
            protectedStone -> PlayerId.Two,
            Position(0, 1) -> PlayerId.Two,
            Position(0, 2) -> PlayerId.Two,
            Position(1, 0) -> PlayerId.Two
          )
        )

      RemoveCommand(protectedStone).execute(state).isFailure shouldBe true
    }
  }

  "RemoveCommand.undo" should {

    "restore the removed stone and the previous turn" in {
      val target =
        Position(1, 0)

      val state =
        stateWith(
          Seq(
            Position(0, 0) -> PlayerId.One,
            Position(0, 1) -> PlayerId.One,
            Position(0, 2) -> PlayerId.One,
            target -> PlayerId.Two,
            Position(1, 1) -> PlayerId.Two,
            Position(1, 2) -> PlayerId.Two
          )
        )

      val afterRemoval =
        RemoveCommand(target).execute(state).get

      val afterUndo =
        RemoveCommand(target).undo(afterRemoval)

      afterUndo.isSuccess shouldBe true
      afterUndo.get.board.placedStones.get(target) shouldBe Some(PlayerId.Two)
      afterUndo.get.player2.stonesOnBoard shouldBe 3
      afterUndo.get.currentPlayer shouldBe PlayerId.One
    }

    "fail when undo is called before a stone was removed" in {
      val target =
        Position(1, 0)

      val state =
        stateWith(Seq(target -> PlayerId.Two))

      RemoveCommand(target).undo(state).isFailure shouldBe true
    }
  }
