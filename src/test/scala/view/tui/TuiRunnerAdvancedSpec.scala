package view.tui

import controller.{BoardViewModel, GameObserver, IController, StonePlacement}
import model.game.GameState
import org.scalatest.matchers.should.Matchers
import org.scalatest.wordspec.AnyWordSpec

import scala.util.{Failure, Success, Try}

class TuiRunnerAdvancedSpec extends AnyWordSpec with Matchers:

  private val emptyViewModel =
    BoardViewModel(
      rows = Seq("+"),
      boardSize = 1,
      stones = Seq.empty[StonePlacement],
      nextPlayerNumber = 1
    )

  private class UndoController extends IController:
    var gameOver: Boolean = false
    var undoCalls: Int = 0
    var handleInputCalls: Int = 0

    def isGameOver: Boolean =
      gameOver

    def boardViewModel: BoardViewModel =
      emptyViewModel

    def currentPrompt: String =
      "prompt> "

    def handleInput(input: String): Try[Unit] =
      handleInputCalls = handleInputCalls + 1
      gameOver = true
      Success(())

    def welcomeMessage: String =
      "Welcome"

    def undo(): Try[Unit] =
      undoCalls = undoCalls + 1
      gameOver = true
      Failure(new RuntimeException("undo was handled by TuiRunner"))

    def saveGame(customName: String): Try[Unit] =
      Success(())

    def loadGame(fileName: String): Try[Unit] =
      Success(())

  private class NullInputController extends IController:
    private var gameOverChecks: Int = 0

    def isGameOver: Boolean =
      gameOverChecks = gameOverChecks + 1
      gameOverChecks > 1

    def boardViewModel: BoardViewModel =
      emptyViewModel

    def currentPrompt: String =
      "prompt> "

    def handleInput(input: String): Try[Unit] =
      if input == null then
        throw new RuntimeException("null input must not be forwarded")

      Success(())

    def welcomeMessage: String =
      "Welcome"

    def undo(): Try[Unit] =
      Success(())

    def saveGame(customName: String): Try[Unit] =
      Success(())

    def loadGame(fileName: String): Try[Unit] =
      Success(())

  "TuiRunner" should {

    "route undo directly to controller.undo instead of handleInput" in {
      val controller =
        new UndoController

      val runner =
        TuiRunner(controller, () => "undo")

      val out =
        new java.io.ByteArrayOutputStream()

      Console.withOut(out) {
        runner.run()
      }

      controller.undoCalls shouldBe 1
      controller.handleInputCalls shouldBe 0
      out.toString should include("undo was handled by TuiRunner")
    }

    "ignore a null input line" in {
      val controller =
        new NullInputController

      val runner =
        TuiRunner(controller, () => null)

      val out =
        new java.io.ByteArrayOutputStream()

      noException should be thrownBy {
        Console.withOut(out) {
          runner.run()
        }
      }
    }

    "print the update separator when an observer update arrives" in {
      val controller =
        new UndoController

      val runner =
        TuiRunner(controller, () => "undo")

      val out =
        new java.io.ByteArrayOutputStream()

      Console.withOut(out) {
        runner.update()
      }

      out.toString should include("--- Update ---")
    }
  }
