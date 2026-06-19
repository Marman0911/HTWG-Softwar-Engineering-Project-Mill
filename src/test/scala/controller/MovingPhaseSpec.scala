package controller

import model.board.Board
import model.board.Position
import model.game.GameState
import org.scalatest.flatspec.AnyFlatSpec
import org.scalatest.matchers.should.Matchers

class MovingPhaseSpec extends AnyFlatSpec with Matchers:

  val parsePos: (String, Board) => Option[Position] =
    (_, _) => Some(Position(0, 0))

  val phase: MovingPhase =
    MovingPhase(parsePos)

  val state: GameState =
    GameState()

  "MovingPhase" should "always return Left (not yet implemented)" in:
    phase.handleInput("a1", state).isLeft shouldBe true

  it should "return the not implemented message" in:
    phase.handleInput("a1", state) shouldBe Left("Moving phase: not yet implemented")

  it should "return the correct prompt" in:
    phase.prompt(GameState()) shouldBe "[Moving] Enter move (from to): "

  it should "return itself from next (stays in MovingPhase)" in:
    phase.next(state) shouldBe a[MovingPhase]