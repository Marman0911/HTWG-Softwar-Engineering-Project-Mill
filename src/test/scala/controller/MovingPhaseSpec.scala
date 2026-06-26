package controller

import controller.command.MoveCommand
import model.board.Board
import model.board.Position
import model.game.GameState
import org.scalatest.flatspec.AnyFlatSpec
import org.scalatest.matchers.should.Matchers

import scala.util.{Failure, Success}

class MovingPhaseSpec extends AnyFlatSpec with Matchers:

  private val start: Position =
    Position(0, 0)

  private val target: Position =
    Position(0, 1)

  private val successParsePos: (String, Board) => Option[Position] =
    (input, _) =>
      input match
        case "a1" => Some(start)
        case "d1" => Some(target)
        case _    => None

  private val state: GameState =
    GameState()

  "MovingPhase" should "create a MoveCommand for two valid positions" in:
    val phase =
      MovingPhase(successParsePos)

    phase.handleInput("a1 d1", state) shouldBe
      Success(MoveCommand(start, target))

  it should "return Failure when only one position is entered" in:
    val phase =
      MovingPhase(successParsePos)

    phase.handleInput("a1", state) match
      case Failure(error) =>
        error.getMessage shouldBe GameMessages.invalidMove

      case Success(_) =>
        fail("Expected Failure when only one position is entered.")

  it should "return Failure when a position cannot be parsed" in:
    val phase =
      MovingPhase(successParsePos)

    phase.handleInput("a1 invalid", state) match
      case Failure(error) =>
        error.getMessage shouldBe GameMessages.invalidMove

      case Success(_) =>
        fail("Expected Failure when one position cannot be parsed.")

  it should "return the correct prompt for player one" in:
    val phase =
      MovingPhase(successParsePos)

    phase.prompt(state) shouldBe
      "Player 1 move: start target (e.g. a1 d1): "

  it should "return itself from next" in:
    val phase =
      MovingPhase(successParsePos)

    phase.next(state) shouldBe phase
