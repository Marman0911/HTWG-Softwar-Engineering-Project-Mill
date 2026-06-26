package controller

import model.board.BoardComponent
import model.board.Position
import model.game.GameComponent
import model.game.GameState
import model.player.PlayerComponent
import model.player.PlayerId
import org.scalatest.matchers.should.Matchers
import org.scalatest.wordspec.AnyWordSpec

import scala.util.{Failure, Success}

class GameControllerSpec extends AnyWordSpec with Matchers {

  private def freshController: GameController =
    GameController()

  private class RecordingObserver extends GameObserver {
    var updateCalls = 0

    def update(): Unit =
      updateCalls = updateCalls + 1
  }

  "GameController.parseInput" should {

    "parse a standard letter-number coordinate" in {
      val board = BoardComponent.create(3)

      freshController.parseInput("a1", board) should be(Some(Position(0, 0)))
    }

    "parse number-letter coordinate order" in {
      val board = BoardComponent.create(3)

      freshController.parseInput("1a", board) should be(Some(Position(0, 0)))
    }

    "accept boundary coordinate g7" in {
      val board = BoardComponent.create(3)

      freshController.parseInput("g7", board) should be(Some(Position(0, 4)))
    }

    "sanitize whitespace and symbols" in {
      val board = BoardComponent.create(3)

      freshController.parseInput("  A1!!  ", board) should be(Some(Position(0, 0)))
    }

    "return None for too short inputs" in {
      val board = BoardComponent.create(3)

      freshController.parseInput("", board) should be(None)
      freshController.parseInput("a", board) should be(None)
    }

    "return None when row number is not numeric" in {
      val board = BoardComponent.create(3)

      freshController.parseInput("ab", board) should be(None)
    }

    "return None for out-of-range column" in {
      val board = BoardComponent.create(3)

      freshController.parseInput("z1", board) should be(None)
    }

    "return None for out-of-range row" in {
      val board = BoardComponent.create(3)

      freshController.parseInput("a0", board) should be(None)
      freshController.parseInput("a9", board) should be(None)
    }

    "reject the immediate row boundaries around the valid range" in {
      val board = BoardComponent.create(3)

      freshController.parseInput("d0", board) should be(None)
      freshController.parseInput("d8", board) should be(None)
    }

    "reject the immediate column boundary above the valid range" in {
      val board = BoardComponent.create(3)

      freshController.parseInput("h4", board) should be(None)
    }

    "accept exact board boundaries" in {
      val board = BoardComponent.create(3)

      freshController.parseInput("a7", board) should be(Some(Position(0, 6)))
      freshController.parseInput("g1", board) should be(Some(Position(0, 2)))
    }

    "return None for coordinates that are not board intersections" in {
      val board = BoardComponent.create(3)

      freshController.parseInput("b1", board) should be(None)
    }

  }

  "GameController.currentPrompt" should {

    "render player one prompt at game start" in {
      freshController.currentPrompt should be("Player 1 enter position (e.g. a1): ")
    }

    "render player two prompt after player one moves" in {
      val ctrl = freshController

      ctrl.handleInput("a1")

      ctrl.currentPrompt should be("Player 2 enter position (e.g. a1): ")
    }

  }

  "GameController.welcomeMessage" should {

    "contain the expected title" in {
      freshController.welcomeMessage should be("Welcome to Nine Men's Morris!")
    }

  }

  "GameController.shouldContinue" should {

    "be true while both players have not lost" in {
      val state = GameComponent.create(
        BoardComponent.create(),
        PlayerComponent.create(PlayerId.One),
        PlayerComponent.create(PlayerId.Two),
        PlayerId.One
      )

      freshController.shouldContinue(state) should be(true)
    }

    "be false when player one has lost" in {
      val lostP1 = PlayerComponent.create(
        PlayerId.One,
        stonesInHand = 2,
        stonesOnBoard = 0
      )

      val state = GameComponent.create(
        BoardComponent.create(),
        lostP1,
        PlayerComponent.create(PlayerId.Two),
        PlayerId.One
      )

      freshController.shouldContinue(state) should be(false)
    }

    "be false when player two has lost" in {
      val lostP2 = PlayerComponent.create(
        PlayerId.Two,
        stonesInHand = 2,
        stonesOnBoard = 0
      )

      val state = GameComponent.create(
        BoardComponent.create(),
        PlayerComponent.create(PlayerId.One),
        lostP2,
        PlayerId.One
      )

      freshController.shouldContinue(state) should be(false)
    }

  }

  "GameController.reverseCoords" should {

    "contain known reverse mappings" in {
      val board = BoardComponent.create(3)
      val reverse = freshController.reverseCoords(board)

      reverse((0, 0)) should be(Position(0, 0))
      reverse((0, 15)) should be(Position(0, 1))
      reverse((6, 0)) should be(Position(0, 7))
    }

  }

  "GameController.handleInput state transitions" should {

    "return invalid message for invalid input" in {
      val ctrl = freshController
      ctrl.handleInput("xx") match {
        case Failure(exception) => exception.getMessage should be("Invalid position.")
        case Success(_)         => fail("Expected Failure for invalid input")
      }
    }

    "return occupied message for occupied position" in {
      val ctrl = freshController
      ctrl.handleInput("a1") should be(Success(()))
      ctrl.handleInput("a1") match {
        case Failure(exception) => exception.getMessage shouldBe GameMessages.occupiedPosition
        case Success(_)         => fail("Expected Failure for occupied position")
      }
    }

    "advance state after a valid move" in {
      val ctrl = freshController
      ctrl.handleInput("a1") should be(Success(()))

      ctrl.boardViewModel.stones should not be empty
      ctrl.boardViewModel.stones.head.playerNumber should be(1)
      ctrl.currentPrompt should include("Player 2")
    }

    "switch to player one when current player was two" in {
      val ctrl = freshController
      ctrl.handleInput("a1") should be(Success(())) // Spieler 1 setzt
      ctrl.handleInput("d1") should be(Success(())) // Spieler 2 setzt
      ctrl.currentPrompt should include("Player 1")
    }

  }

  "GameController.handleInput" should {

    "report isGameOver true when a player has lost" in {
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

      val ctrl = GameController()
      val observer = RecordingObserver()

      ctrl.addObserver(observer)

      ctrl.isGameOver should be(true)
      observer.updateCalls should be(0)
    }

    "evaluate continue condition independently of input" in {
      val ctrl = freshController

      ctrl.isGameOver should be(false)
    }

    "notify observers after a valid move" in {
      val ctrl = freshController
      val observer = RecordingObserver()

      ctrl.addObserver(observer)

      ctrl.handleInput("a1") should be(Success(()))

      observer.updateCalls should be(1)
      ctrl.boardViewModel.stones should not be empty
      ctrl.boardViewModel.stones.head.playerNumber should be(1)
    }

    "return Failure for invalid input without notifying observers" in {
      val ctrl = freshController
      val observer = RecordingObserver()

      ctrl.addObserver(observer)

      ctrl.handleInput("xx") match
        case Failure(exception) => exception.getMessage should be("Invalid position.")
        case Success(_)         => fail("Expected Failure for invalid input")

      observer.updateCalls should be(0)
    }

    "return Failure for occupied position without notifying observers" in {
      val ctrl = freshController

      ctrl.handleInput("a1")

      val observer = RecordingObserver()
      ctrl.addObserver(observer)

      ctrl.handleInput("a1") match
        case Failure(exception) => exception.getMessage shouldBe GameMessages.occupiedPosition
        case Success(_)         => fail("Expected Failure for occupied position")

      observer.updateCalls should be(0)
    }

  }

  "GameController.undo" should {

    "return a Failure with message when history is empty" in {
      val ctrl = freshController

      ctrl.undo() match
        case Failure(exception) => exception.getMessage should be("Nothing to undo.")
        case Success(_)         => fail("Expected Failure when history is empty")
    }

    "restore the previous state and decrease history when executing successfully" in {
      val ctrl = freshController

      ctrl.handleInput("a1") should be(Success(()))
      ctrl.boardViewModel.stones.map(_.playerNumber) should contain(1)

      val undoResult = ctrl.undo()

      undoResult should be(Success(()))
      ctrl.boardViewModel.stones should be(empty)
    }

    "trigger notifyObservers upon a successful undo execution" in {
      val ctrl = freshController
      val observer = RecordingObserver()

      ctrl.handleInput("a1") should be(Success(()))
      ctrl.addObserver(observer)

      observer.updateCalls should be(0)

      ctrl.undo() should be(Success(()))
      observer.updateCalls should be(1)
    }

  }

  "GameController.handleInput with 'undo'" should {

    "intercept the command 'undo' and route it internally to the undo logic" in {
      val ctrl = freshController

      ctrl.handleInput("a1") should be(Success(()))
      ctrl.handleInput("  UNDO  ") should be(Success(()))

      ctrl.boardViewModel.stones should be(empty)
    }

  }
}