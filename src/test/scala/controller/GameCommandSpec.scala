package controller.command

import org.scalatest.flatspec.AnyFlatSpec
import org.scalatest.matchers.should.Matchers
import model.{GameState, MillBoard, Position}

class GameCommandSpec extends AnyFlatSpec with Matchers:

  // Dummy-Implementierung um den Trait zu testen
  class AlwaysSuccessCommand extends GameCommand:
    def execute(state: GameState): Option[GameState] = Some(state)

  class AlwaysFailCommand extends GameCommand:
    def execute(state: GameState): Option[GameState] = None

  val state = GameState()

  "A GameCommand" should "return Some(GameState) on success" in:
    val cmd = AlwaysSuccessCommand()
    cmd.execute(state) shouldBe defined

  it should "return None on failure" in:
    val cmd = AlwaysFailCommand()
    cmd.execute(state) shouldBe None

  it should "not modify state when returning None" in:
    val cmd = AlwaysFailCommand()
    cmd.execute(state) shouldBe None

  it should "return a GameState when successful" in:
    val cmd = AlwaysSuccessCommand()
    cmd.execute(state) shouldBe Some(state)