package controller

import controller.command.GameCommand
import model.game.GameState
import org.scalatest.matchers.should.Matchers
import org.scalatest.wordspec.AnyWordSpec

import scala.util.{Failure, Success, Try}

class GamePhaseSpec extends AnyWordSpec with Matchers:

  private val state =
    GameState()

  private val successfulCommand =
    new GameCommand:

      override def execute(state: GameState): Try[GameState] =
        Success(state)

      override def undo(state: GameState): Try[GameState] =
        Success(state)

  private val successfulPhase =
    new GamePhase:

      override def handleInput(
          input: String,
          state: GameState
      ): Try[GameCommand] =
        Success(successfulCommand)

      override def prompt(state: GameState): String =
        "Test prompt"

      override def next(state: GameState): GamePhase =
        this

  private val failingPhase =
    new GamePhase:

      override def handleInput(
          input: String,
          state: GameState
      ): Try[GameCommand] =
        Failure(GameException("Test failure"))

      override def prompt(state: GameState): String =
        "Test prompt"

      override def next(state: GameState): GamePhase =
        this

  "A GamePhase" should {

    "return Success on success" in {
      successfulPhase.handleInput("a1", state).isSuccess shouldBe true
    }

    "return Failure on failure" in {
      failingPhase.handleInput("a1", state).isFailure shouldBe true
    }

    "return a non-empty prompt" in {
      successfulPhase.prompt(state).nonEmpty shouldBe true
    }

    "return a GamePhase from next" in {
      successfulPhase.next(state) shouldBe a[GamePhase]
    }
  }