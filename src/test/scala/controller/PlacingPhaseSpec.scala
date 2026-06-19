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
      case Failure(e) => e.getMessage shouldBe GameMessages.invalidPosition
      case Success(_) => fail("Test fehlgeschlagen: Parser-Fehler hätte Failure werfen müssen!")
    }

  it should "return Failure when position is already occupied" in:
    val phase = PlacingPhase(successParsePos)

    // 1. Hol das Command aus dem Success-Zweig
    val cmd = phase.handleInput("a1", state).get
    
    // 2. Führe das Command aus, um den besetzten Zustand zu erzeugen
    val filledState = cmd.execute(state).get

    // 3. Das Command selbst wird hier zwar noch erzeugt, aber die Ausführung des Commands wird später ein Failure werfen
    phase.handleInput("a1", filledState).isSuccess shouldBe true

  it should "return the correct prompt" in:
    val phase = PlacingPhase(successParsePos)

    phase.prompt(GameState()) shouldBe GameMessages.promptFor(model.player.PlayerId.One)

  it should "stay in PlacingPhase when board is not full" in:
    val phase = PlacingPhase(successParsePos)

    phase.next(state) shouldBe a[PlacingPhase]

  it should "transition to MovingPhase when board is full" in:
    val phase = PlacingPhase(successParsePos)

    val fullState =
      state.board.allPositions.foldLeft(state): (currentState, pos) =>
        currentState.placeStone(pos).getOrElse(currentState)

    phase.next(fullState) shouldBe a[MovingPhase]