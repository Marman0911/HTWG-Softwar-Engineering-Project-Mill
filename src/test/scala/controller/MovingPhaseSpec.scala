package controller

import org.scalatest.flatspec.AnyFlatSpec
import org.scalatest.matchers.should.Matchers
import model.{GameState, MillBoard, Position}

class MovingPhaseSpec extends AnyFlatSpec with Matchers:

  val parsePos: (String, MillBoard) => Option[Position] =
    (_, _) => Some(Position(0, 0))

  val phase = MovingPhase(parsePos)
  val state = GameState()

  "MovingPhase" should "always return Left (not yet implemented)" in:
    phase.handleInput("a1", state) shouldBe a[Left[?, ?]]

  it should "return the not implemented message" in:
    phase.handleInput("a1", state) shouldBe Left("Moving phase: not yet implemented")

  it should "return the correct prompt" in:
    phase.prompt shouldBe "[Moving] Enter move (from to): "

  it should "return itself from next (stays in MovingPhase)" in:
    phase.next(state) shouldBe a[MovingPhase]