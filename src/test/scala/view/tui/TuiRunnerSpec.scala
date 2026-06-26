package view.tui

import view.tui.TuiRunner
import controller.GameController
import model.board.BoardComponent
import model.game.GameComponent
import model.player.PlayerComponent
import model.player.PlayerId
import org.scalatest.matchers.should.Matchers
import org.scalatest.wordspec.AnyWordSpec

class TuiRunnerSpec extends AnyWordSpec with Matchers:

  "TuiRunner.run" should {

    /*"exit immediately when the game is already over" in {
      val controller = GameController()
      controller.isGameOver.should(be(false))
      var inputCalled = false
      val runner = TuiRunner(
        controller,
        () =>
          inputCalled = true
          ""
      )
      val out = new java.io.ByteArrayOutputStream()
      Console.withOut(out) {
        runner.run()
      }
      inputCalled.should(be(false))
    }*/

    "process a valid move and continue until game over" in {
      val controller =
        GameController()

      val inputs =
        Iterator("a1")

      val runner =
        TuiRunner(
          controller,
          () =>
            if inputs.hasNext then inputs.next()
            else throw new RuntimeException("stop")
        )

      val out =
        new java.io.ByteArrayOutputStream()

      an[RuntimeException] should be thrownBy {
        Console.withOut(out) {
          runner.run()
        }
      }

      controller.boardViewModel.stones should not be empty
    }

    "print error message for invalid input and retry" in {
      val controller =
        GameController()

      val inputs =
        Iterator("xx", "a1")

      val runner =
        TuiRunner(
          controller,
          () =>
            if inputs.hasNext then inputs.next()
            else throw new RuntimeException("stop")
        )

      val out =
        new java.io.ByteArrayOutputStream()

      an[RuntimeException] should be thrownBy {
        Console.withOut(out) {
          runner.run()
        }
      }

      out.toString should include("Invalid position.")
    }
  }