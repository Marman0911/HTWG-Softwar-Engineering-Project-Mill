package tui

import controller.GameController
import model.GameState
import model.MillBoard
import model.Player
import model.PlayerId
import org.scalatest.matchers.should.Matchers
import org.scalatest.wordspec.AnyWordSpec

class TuiRunnerSpec extends AnyWordSpec with Matchers:

  "TuiRunner.run" should {

    "exit immediately when the game is already over" in {
      val lostP1        = Player(PlayerId.One, stonesInHand = 2, stonesOnBoard = 0)
      val terminalState = GameState(MillBoard(), lostP1, Player(PlayerId.Two), PlayerId.One)
      val controller    = GameController(terminalState)

      var inputCalled = false
      val runner = TuiRunner(controller, () => { inputCalled = true; "" })

      val out = new java.io.ByteArrayOutputStream()
      Console.withOut(out) { runner.run() }

      inputCalled        should be(false)
      controller.isGameOver should be(true)
    }

    "process a valid move and continue until game over" in {
      val controller = GameController()

      // Provide one valid move then an always-false guard via a controller that
      // ends the game by producing terminal state after the first move.
      // We stop after one move by throwing from the second read.
      val inputs = Iterator("a1")
      val runner = TuiRunner(controller, () =>
        if inputs.hasNext then inputs.next()
        else throw new RuntimeException("stop")
      )

      val out = new java.io.ByteArrayOutputStream()
      an[RuntimeException] should be thrownBy {
        Console.withOut(out) { runner.run() }
      }

      controller.boardViewModel.stones should not be empty
    }

    "print error message for invalid input and retry" in {
      val controller = GameController()
      val inputs     = Iterator("xx", "a1")
      val runner     = TuiRunner(controller, () =>
        if inputs.hasNext then inputs.next()
        else throw new RuntimeException("stop")
      )

      val out = new java.io.ByteArrayOutputStream()
      an[RuntimeException] should be thrownBy {
        Console.withOut(out) { runner.run() }
      }

      out.toString should include("Invalid position.")
    }
  }
