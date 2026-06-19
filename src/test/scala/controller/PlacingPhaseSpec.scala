package controller

import model.board.Board
import model.board.Position
import model.game.GameState
import org.scalatest.flatspec.AnyFlatSpec
import org.scalatest.matchers.should.Matchers

class PlacingPhaseSpec extends AnyFlatSpec with Matchers:

  val validPos: Position =
    Position(0, 0)

  val successParsePos: (String, Board) => Option[Position] =
    (_, _) => Some(validPos)

  val failParsePos: (String, Board) => Option[Position] =
    (_, _) => None

  val state: GameState =
    GameState()

  "PlacingPhase" should "return Right when position is valid and empty" in:
    val phase = PlacingPhase(successParsePos)

    phase.handleInput("a1", state).isRight shouldBe true

  it should "return Left when position cannot be parsed" in:
    val phase = PlacingPhase(failParsePos)

    phase.handleInput("invalid", state).isLeft shouldBe true

  it should "return Left with invalidPosition message on bad input" in:
    val phase = PlacingPhase(failParsePos)

    phase.handleInput("invalid", state) shouldBe Left("Invalid position.")

  it should "return Left when position is already occupied" in:
    val phase = PlacingPhase(successParsePos)

    val filledState =
      phase.handleInput("a1", state).getOrElse(state)

    phase.handleInput("a1", filledState) shouldBe Left("Position occupied.")

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