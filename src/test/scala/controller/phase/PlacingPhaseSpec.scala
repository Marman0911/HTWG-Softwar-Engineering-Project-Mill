package controller.phase

import org.scalatest.flatspec.AnyFlatSpec
import org.scalatest.matchers.should.Matchers
import model.{GameState, MillBoard, Position, PlayerId}

class PlacingPhaseSpec extends AnyFlatSpec with Matchers:

  val validPos = Position(0, 0)

  val successParsePos: (String, MillBoard) => Option[Position] =
    (_, _) => Some(validPos)

  val failParsePos: (String, MillBoard) => Option[Position] =
    (_, _) => None

  val state = GameState()

  "PlacingPhase" should "return Right when position is valid and empty" in:
    val phase = PlacingPhase(successParsePos)
    phase.handleInput("a1", state) shouldBe a[Right[_, _]]

  it should "return Left when position cannot be parsed" in:
    val phase = PlacingPhase(failParsePos)
    phase.handleInput("invalid", state) shouldBe a[Left[_, _]]

  it should "return Left with invalidPosition message on bad input" in:
    val phase = PlacingPhase(failParsePos)
    phase.handleInput("invalid", state) shouldBe Left("Invalid position.")

  it should "return Left when position is already occupied" in:
    val phase       = PlacingPhase(successParsePos)
    val filledState = phase.handleInput("a1", state).getOrElse(state)
    phase.handleInput("a1", filledState) shouldBe Left("Position occupied.")

  it should "return the correct prompt" in:
    val phase = PlacingPhase(successParsePos)
    phase.prompt shouldBe "[Placing] Enter position (e.g. a1): "

  it should "stay in PlacingPhase when board is not full" in:
    val phase = PlacingPhase(successParsePos)
    phase.next(state) shouldBe a[PlacingPhase]

  it should "transition to MovingPhase when board is full" in:
    val phase = PlacingPhase(successParsePos)
    // Board füllen durch wiederholtes placeStone statt copy
    val fullState = state.board.allPositions.foldLeft(state): (s, pos) =>
      s.board.placeStone(pos, PlayerId.One)
        .map(newBoard => s.copy(board = newBoard))
        .getOrElse(s)
    phase.next(fullState) shouldBe a[MovingPhase]