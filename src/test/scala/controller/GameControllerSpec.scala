package controller

import model.*
import view.BoardView
import org.scalatest.matchers.should.Matchers
import org.scalatest.wordspec.AnyWordSpec
import java.io.ByteArrayInputStream
import java.io.ByteArrayOutputStream

class GameControllerSpec extends AnyWordSpec with Matchers:

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

    "keep state and return invalid message for invalid input" in {
      val state = GameState()
      val view = BoardView()

      val (nextState, messages) = GameController.handleTurnInput(state, "xx", view)

      nextState should be(state)
      messages should be(Seq("Invalid position."))
    }

    "keep state and return occupied message for occupied position" in {
      val view = BoardView()
      val state = GameState().placeStone(Position(0, 0)).get

      val (nextState, messages) = GameController.handleTurnInput(state, "a1", view)

      nextState should be(state)
      messages should be(Seq("Position occupied."))
    }

    "advance state and print next player after a valid move" in {
      val state = GameState()
      val view = BoardView()

      val (nextState, messages) = GameController.handleTurnInput(state, "a1", view)

      nextState should not be state
      nextState.board.stones(Position(0, 0)) should be(Some(PlayerId.One))
      nextState.currentPlayer should be(PlayerId.Two)
      messages.size should be(2)
      messages.head should include("+")
      messages(1) should be("Next: Player 2")
    }

    "output next player one when current player was two" in {
      val view = BoardView()
      val state = GameState().placeStone(Position(0, 0)).get

      val (nextState, messages) = GameController.handleTurnInput(state, "d1", view)

      nextState.currentPlayer should be(PlayerId.One)
      messages(1) should be("Next: Player 1")
    }
  }

  "GameController.start" should {

    "enter the game loop while both players are active" in {
      val in = ByteArrayInputStream(Array.emptyByteArray)
      val out = ByteArrayOutputStream()

      assertThrows[NullPointerException] {
        Console.withIn(in) {
          Console.withOut(out) {
            GameController.start()
          }
        }
      }

      val printed = out.toString("UTF-8")
      printed should include("Welcome to Nine Men's Morris!")
      printed should include("Player 1 enter position (e.g. a1):")
    }
  }
