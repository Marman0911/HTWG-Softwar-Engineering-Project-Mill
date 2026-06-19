package controller

import model.board.Board
import model.board.Position
import model.game.GameState
import org.scalatest.flatspec.AnyFlatSpec
import org.scalatest.matchers.should.Matchers
import scala.util.{Failure, Success}

class PlacingPhaseSpec extends AnyFlatSpec with Matchers:

  val validPos: Position =
    Position(0, 0)

  val successParsePos: (String, Board) => Option[Position] =
    (_, _) => Some(validPos)

  val failParsePos: (String, Board) => Option[Position] =
    (_, _) => None

  val state: GameState =
    GameState()

  "PlacingPhase" should "return Success when position is valid and empty" in:
    val phase = PlacingPhase(successParsePos)

    phase.handleInput("a1", state).isSuccess shouldBe true

  it should "return Failure when position cannot be parsed" in:
    val phase = PlacingPhase(failParsePos)

    phase.handleInput("invalid", state).isFailure shouldBe true

  it should "return Failure with invalidPosition message on bad input" in:
    val phase = PlacingPhase(failParsePos)

    phase.handleInput("invalid", state) match {
      case Failure(e) => e.getMessage shouldBe "Invalid position."
      case Success(_) => fail("Test fehlgeschlagen: Parser-Fehler hätte Failure werfen müssen!")
    }

  it should "return Failure when position is already occupied" in:
    val phase = PlacingPhase(successParsePos)

    val filledState =
      phase.handleInput("a1", state).get

    phase.handleInput("a1", filledState) match {
      case Failure(e) => e.getMessage shouldBe "Position occupied."
      case Success(_) => fail("Test fehlgeschlagen: Feld hätte besetzt sein müssen!")
    }

  it should "return the correct prompt" in:
    val phase = PlacingPhase(successParsePos)

    phase.prompt(GameState()) shouldBe "Player 1 enter position (e.g. a1): "

  it should "stay in PlacingPhase when board is not full" in:
    val phase = PlacingPhase(successParsePos)

    phase.next(state) shouldBe a[PlacingPhase]

  it should "transition to MovingPhase when board is full" in:
    val phase = PlacingPhase(successParsePos)

    val fullState =
      state.board.allPositions.foldLeft(state): (currentState, pos) =>
        currentState.placeStone(pos).getOrElse(currentState)

    phase.next(fullState) shouldBe a[MovingPhase]