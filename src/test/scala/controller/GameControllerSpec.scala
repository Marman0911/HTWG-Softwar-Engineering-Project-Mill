package controller

import model.GameState
import model.MillBoard
import model.Player
import model.PlayerId
import model.Position
import org.scalatest.matchers.should.Matchers
import org.scalatest.wordspec.AnyWordSpec
import view.BoardViewModel

class GameControllerSpec extends AnyWordSpec with Matchers:

  private class RecordingObserver extends GameObserver:
    var updateCalls = 0
    var lastViewModel: Option[BoardViewModel] = None

    def update(viewModel: BoardViewModel): Unit =
      updateCalls = updateCalls + 1
      lastViewModel = Some(viewModel)

  "GameController.parseInput" should {

    "parse a standard letter-number coordinate" in {
      val board = MillBoard(3)
      GameController.parseInput("a1", board) should be(Some(Position(0, 0)))
    }

    "parse number-letter coordinate order" in {
      val board = MillBoard(3)
      GameController.parseInput("1a", board) should be(Some(Position(0, 0)))
    }

    "accept boundary coordinate g7" in {
      val board = MillBoard(3)
      GameController.parseInput("g7", board) should be(Some(Position(0, 4)))
    }

    "sanitize whitespace and symbols" in {
      val board = MillBoard(3)
      GameController.parseInput("  A1!!  ", board) should be(Some(Position(0, 0)))
    }

    "return None for too short inputs" in {
      val board = MillBoard(3)
      GameController.parseInput("", board) should be(None)
      GameController.parseInput("a", board) should be(None)
    }

    "return None when row number is not numeric" in {
      val board = MillBoard(3)
      GameController.parseInput("ab", board) should be(None)
    }

    "return None for out-of-range column" in {
      val board = MillBoard(3)
      GameController.parseInput("z1", board) should be(None)
    }

    "return None for out-of-range row" in {
      val board = MillBoard(3)
      GameController.parseInput("a0", board) should be(None)
      GameController.parseInput("a9", board) should be(None)
    }

    "reject the immediate row boundaries around the valid range" in {
      val board = MillBoard(3)

      GameController.parseInput("d0", board) should be(None)
      GameController.parseInput("d8", board) should be(None)
    }

    "reject the immediate column boundary above the valid range" in {
      val board = MillBoard(3)

      GameController.parseInput("h4", board) should be(None)
    }

    "accept exact board boundaries" in {
      val board = MillBoard(3)

      GameController.parseInput("a7", board) should be(Some(Position(0, 6)))
      GameController.parseInput("g1", board) should be(Some(Position(0, 2)))
    }

    "return None for coordinates that are not board intersections" in {
      val board = MillBoard(3)
      GameController.parseInput("b1", board) should be(None)
    }
  }

  "GameController.promptFor" should {

    "render player one prompt" in {
      GameController.promptFor(PlayerId.One) should be("Player 1 enter position (e.g. a1): ")
    }

    "render player two prompt" in {
      GameController.promptFor(PlayerId.Two) should be("Player 2 enter position (e.g. a1): ")
    }
  }

  "GameController.welcomeMessage" should {

    "contain the expected title" in {
      GameController.welcomeMessage should be("Welcome to Nine Men's Morris!")
    }
  }

  "GameController.shouldContinue" should {

    "be true while both players have not lost" in {
      val state = GameState(MillBoard(), Player(PlayerId.One), Player(PlayerId.Two), PlayerId.One)
      GameController.shouldContinue(state) should be(true)
    }

    "be false when player one has lost" in {
      val lostP1 = Player(PlayerId.One, stonesInHand = 2, stonesOnBoard = 0)
      val state = GameState(MillBoard(), lostP1, Player(PlayerId.Two), PlayerId.One)
      GameController.shouldContinue(state) should be(false)
    }

    "be false when player two has lost" in {
      val lostP2 = Player(PlayerId.Two, stonesInHand = 2, stonesOnBoard = 0)
      val state = GameState(MillBoard(), Player(PlayerId.One), lostP2, PlayerId.One)
      GameController.shouldContinue(state) should be(false)
    }
  }

  "GameController.reverseCoords" should {

    "contain known reverse mappings" in {
      val board = MillBoard(3)
      val reverse = GameController.reverseCoords(board)

      reverse((0, 0)) should be(Position(0, 0))
      reverse((0, 15)) should be(Position(0, 1))
      reverse((6, 0)) should be(Position(0, 7))
    }
  }

  "GameController.handleTurnInput" should {

    "return invalid message for invalid input" in {
      val state = GameState()

      val result = GameController.handleTurnInput(state, "xx")

      result should be(Left("Invalid position."))
    }

    "return occupied message for occupied position" in {
      val state = GameState().placeStone(Position(0, 0)).get

      val result = GameController.handleTurnInput(state, "a1")

      result should be(Left("Position occupied."))
    }

    "advance state after a valid move" in {
      val state = GameState()

      val result = GameController.handleTurnInput(state, "a1")
      result shouldBe a[Right[?, ?]]
      val nextState = result.toOption.get

      nextState should not be state
      nextState.board.stones(Position(0, 0)) should be(Some(PlayerId.One))
      nextState.currentPlayer should be(PlayerId.Two)
    }

    "switch to player one when current player was two" in {
      val state = GameState().placeStone(Position(0, 0)).get

      val result = GameController.handleTurnInput(state, "d1")
      result shouldBe a[Right[?, ?]]
      val nextState = result.toOption.get

      nextState.currentPlayer should be(PlayerId.One)
    }
  }

  "runGameLoop" should {

    "stop immediately when the game is already over" in {
      GameController.clearObservers()
      val lostP1 = Player(PlayerId.One, stonesInHand = 2, stonesOnBoard = 0)
      val terminalState = GameState(MillBoard(), lostP1, Player(PlayerId.Two), PlayerId.One)
      val observer = RecordingObserver()
      GameController.addObserver(observer)

      var inputCalled = false
      var promptCalled = false
      var lineCalled = false

      val result = GameController.runGameLoop(
        terminalState,
        () =>
          inputCalled = true
          "a1",
        _ => promptCalled = true,
        _ => lineCalled = true
      )

      result should be(terminalState)
      inputCalled should be(false)
      promptCalled should be(false)
      lineCalled should be(false)
      observer.updateCalls should be(0)

      GameController.removeObserver(observer)
      GameController.clearObservers()
    }

    "evaluate continue condition before the turn loop" in {
      GameController.clearObservers()
      val state = GameState()

      an[RuntimeException] should be thrownBy {
        GameController.runGameLoop(
          state,
          () => throw new RuntimeException("stop-loop"),
          _ => (),
          _ => ()
        )
      }

      GameController.clearObservers()
    }

    "notify observers after a valid move" in {
      GameController.clearObservers()
      val observer = RecordingObserver()
      GameController.addObserver(observer)

      val state = GameState()

      an[RuntimeException] should be thrownBy {
        GameController.runGameLoop(
          state,
          () => "a1",
          _ => (),
          _ => throw new RuntimeException("stop-after-first-update")
        )
      }

      observer.updateCalls should be(1)
      observer.lastViewModel shouldBe defined
      observer.lastViewModel.get.stones should not be empty

      GameController.removeObserver(observer)
      GameController.clearObservers()
    }
  }
