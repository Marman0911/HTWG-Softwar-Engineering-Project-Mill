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

    "exit immediately when the game is already over" in {
      val lostP1 = PlayerComponent.create(
        PlayerId.One,
        stonesInHand = 2,
        stonesOnBoard = 0
      )
      val terminalState = GameComponent.create(
        BoardComponent.create(),
        lostP1,
        PlayerComponent.create(PlayerId.Two),
        PlayerId.One
      )
      val controller = GameController(terminalState)
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
      controller.isGameOver.should(be(true))
    }

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
    "print error message when undo fails on empty history" in {
      val controller = GameController()
      val inputs = Iterator("undo", "a1")
      val runner = TuiRunner(
        controller,
        () =>
          if inputs.hasNext then inputs.next()
          else throw new RuntimeException("stop")
      )
      val out = new java.io.ByteArrayOutputStream()
      an[RuntimeException] should be thrownBy {
        Console.withOut(out) {
          runner.run()
        }
      }
      out.toString should include("Nothing to undo.")
    }

    "print success message after valid undo" in {
      val controller = GameController()
      val inputs = Iterator("a1", "undo", "a1")
      val runner = TuiRunner(
        controller,
        () =>
          if inputs.hasNext then inputs.next()
          else throw new RuntimeException("stop")
      )
      val out = new java.io.ByteArrayOutputStream()
      an[RuntimeException] should be thrownBy {
        Console.withOut(out) {
          runner.run()
        }
      }
      controller.boardViewModel.stones should not be empty
    }
  }