package controller

import controller.command.RemoveCommand
import model.board.{Board, BoardComponent, Position}
import model.game.{GameComponent, GameState}
import model.player.{PlayerComponent, PlayerId}
import org.scalatest.matchers.should.Matchers
import org.scalatest.wordspec.AnyWordSpec

import scala.util.{Failure, Success}

class RemovingPhaseSpec extends AnyWordSpec with Matchers:

  private val positions = Map(
    "a1" -> Position(0, 0),
    "d1" -> Position(0, 1),
    "g1" -> Position(0, 2),
    "a2" -> Position(1, 0),
    "d2" -> Position(1, 1),
    "g2" -> Position(1, 2)
  )

  private val parsePosition: (String, Board) => Option[Position] =
    (input, _) => positions.get(input.trim.toLowerCase)

  private def stateWith(
      stones: Seq[(Position, PlayerId)],
      playerOneStonesInHand: Int = 0,
      playerTwoStonesInHand: Int = 0
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
      PlayerComponent.create(PlayerId.One, playerOneStonesInHand, playerOneOnBoard),
      PlayerComponent.create(PlayerId.Two, playerTwoStonesInHand, playerTwoOnBoard),
      PlayerId.One
    )

  "RemovingPhase.handleInput" should {

    "create a RemoveCommand for an allowed opponent stone" in {
      val state =
        stateWith(
          Seq(
            Position(0, 0) -> PlayerId.One,
            Position(1, 0) -> PlayerId.Two
          )
        )

      val phase =
        RemovingPhase(parsePosition)

      phase.handleInput("a2", state) shouldBe Success(RemoveCommand(Position(1, 0)))
    }

    "reject an input that cannot be parsed" in {
      val state =
        stateWith(Seq(Position(1, 0) -> PlayerId.Two))

      val phase =
        RemovingPhase(parsePosition)

      phase.handleInput("does-not-exist", state) match
        case Failure(error) =>
          error.getMessage shouldBe "Invalid removal position."
        case Success(_) =>
          fail("Expected an invalid removal position to fail.")
    }

    "reject an own stone" in {
      val state =
        stateWith(
          Seq(
            Position(0, 0) -> PlayerId.One,
            Position(1, 0) -> PlayerId.Two
          )
        )

      val phase =
        RemovingPhase(parsePosition)

      phase.handleInput("a1", state) match
        case Failure(error) =>
          error.getMessage shouldBe "Invalid removal. Choose a removable opponent stone."
        case Success(_) =>
          fail("Expected removing an own stone to fail.")
    }

    "reject an opponent stone inside a mill while another opponent stone is outside" in {
      val state =
        stateWith(
          Seq(
            Position(0, 0) -> PlayerId.Two,
            Position(0, 1) -> PlayerId.Two,
            Position(0, 2) -> PlayerId.Two,
            Position(1, 0) -> PlayerId.Two
          )
        )

      val phase =
        RemovingPhase(parsePosition)

      phase.handleInput("a1", state).isFailure shouldBe true
      phase.handleInput("a2", state) shouldBe Success(RemoveCommand(Position(1, 0)))
    }
  }

  "RemovingPhase.next" should {

    "go to MovingPhase when all stones have been placed" in {
      val state =
        stateWith(Seq.empty)

      RemovingPhase(parsePosition).next(state) shouldBe a[MovingPhase]
    }

    "go to PlacingPhase while a player still has stones in hand" in {
      val state =
        stateWith(
          Seq.empty,
          playerOneStonesInHand = 1
        )

      RemovingPhase(parsePosition).next(state) shouldBe a[PlacingPhase]
    }
  }
