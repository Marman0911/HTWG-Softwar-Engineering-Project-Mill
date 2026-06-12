package controller

import org.scalatest.flatspec.AnyFlatSpec
import org.scalatest.matchers.should.Matchers
import model.{GameState, MillBoard, Position}
import controller.GamePhase

class GamePhaseSpec extends AnyFlatSpec with Matchers:

  class AlwaysSuccessPhase extends GamePhase:
    def handleInput(input: String, state: GameState): Either[String, GameState] =
      Right(state)
    def prompt: String = "test prompt"
    def next(state: GameState): GamePhase = this

  class AlwaysFailPhase extends GamePhase:
    def handleInput(input: String, state: GameState): Either[String, GameState] =
      Left("error")
    def prompt: String = "fail prompt"
    def next(state: GameState): GamePhase = this

  val state = GameState()

  "A GamePhase" should "return Right on success" in:
    AlwaysSuccessPhase().handleInput("a1", state) shouldBe Right(state)

  it should "return Left on failure" in:
    AlwaysFailPhase().handleInput("a1", state) shouldBe a[Left[?, ?]]

  it should "return a non-empty prompt" in:
    AlwaysSuccessPhase().prompt should not be empty

  it should "return a GamePhase from next" in:
    AlwaysSuccessPhase().next(state) shouldBe a[GamePhase]