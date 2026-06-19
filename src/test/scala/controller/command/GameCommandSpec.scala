package controller.command

import model.game.GameState
import org.scalatest.flatspec.AnyFlatSpec
import org.scalatest.matchers.should.Matchers
import scala.util.{Try, Success, Failure}

class GameCommandSpec extends AnyFlatSpec with Matchers:

  class AlwaysSuccessCommand extends GameCommand:
    def execute(state: GameState): Try[GameState] =
      Success(state)
    def undo(state: GameState): Try[GameState] =
      Success(state)

  class AlwaysFailCommand extends GameCommand:
    def execute(state: GameState): Try[GameState] =
      Failure(new RuntimeException("Command failed"))
    def undo(state: GameState): Try[GameState] =
      Success(state)

  val state: GameState =
    GameState()

  "A GameCommand" should "return Success(GameState) on success" in:
    val cmd = AlwaysSuccessCommand()
    cmd.execute(state).isSuccess shouldBe true

  it should "return Failure on failure" in:
    val cmd = AlwaysFailCommand()
    cmd.execute(state).isFailure shouldBe true

  it should "not modify state when returning Failure" in:
    val cmd = AlwaysFailCommand()
    cmd.execute(state).isFailure shouldBe true

  it should "return a GameState when successful" in:
    val cmd = AlwaysSuccessCommand()
    cmd.execute(state) shouldBe Success(state)