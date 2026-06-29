package controller

import controller.command.RemoveCommand
import model.board.{Board, BoardComponent, Position}
import model.fileio.{FileIOInterface, JsonFileIO}
import model.game.{GameComponent, GameState}
import model.player.{PlayerComponent, PlayerId}
import org.scalatest.matchers.should.Matchers
import org.scalatest.wordspec.AnyWordSpec

import scala.util.{Failure, Success, Try}

class GameControllerAdvancedSpec extends AnyWordSpec with Matchers:

  private class RecordingObserver extends GameObserver:
    var updateCalls: Int = 0

    def update(): Unit =
      updateCalls = updateCalls + 1

  private class RecordingFileIO(
      loadResult: Try[GameState]
  ) extends FileIOInterface:

    var savedPath: Option[String] = None
    var loadedPath: Option[String] = None

    def save(state: GameState, filePath: String): Unit =
      savedPath = Some(filePath)

    def load(filePath: String): Try[GameState] =
      loadedPath = Some(filePath)
      loadResult

  private class RecordingJsonFileIO(
      loadResult: Try[GameState]
  ) extends JsonFileIO:

    var savedPath: Option[String] = None
    var loadedPath: Option[String] = None

    override def save(state: GameState, filePath: String): Unit =
      savedPath = Some(filePath)

    override def load(filePath: String): Try[GameState] =
      loadedPath = Some(filePath)
      loadResult

  private def stateWith(
      stones: Seq[(Position, PlayerId)],
      playerOneStonesInHand: Int,
      playerTwoStonesInHand: Int,
      currentPlayer: PlayerId
  ): GameState =
    val board =
      stones.foldLeft(BoardComponent.create(3)): (currentBoard, stone) =>
        currentBoard.placeStone(stone._1, stone._2).get

    val playerOneOnBoard =
      stones.count(_._2 == PlayerId.One)

    val playerTwoOnBoard =
      stones.count(_._2 == PlayerId.Two)

    GameComponent.create(
      board,
      PlayerComponent.create(
        PlayerId.One,
        stonesInHand = playerOneStonesInHand,
        stonesOnBoard = playerOneOnBoard
      ),
      PlayerComponent.create(
        PlayerId.Two,
        stonesInHand = playerTwoStonesInHand,
        stonesOnBoard = playerTwoOnBoard
      ),
      currentPlayer
    )

  "GameController.saveGame" should {

    "use a trimmed custom name and XML extension for a non-JSON file IO" in {
      val fileIO =
        new RecordingFileIO(Success(GameState()))

      val controller =
        new GameController(GameState(), fileIO)

      controller.saveGame("  my-save  ") shouldBe Success(())
      fileIO.savedPath shouldBe Some("saves/my-save.xml")
    }

    "create an automatic XML file name when no name is given" in {
      val fileIO =
        new RecordingFileIO(Success(GameState()))

      val controller =
        new GameController(GameState(), fileIO)

      controller.saveGame("   ") shouldBe Success(())

      fileIO.savedPath.get.matches(
        """saves/millbc_\d{8}_\d{4}\.xml"""
      ) shouldBe true
    }

    "use the JSON extension for a JSON file IO" in {
      val fileIO =
        new RecordingJsonFileIO(Success(GameState()))

      val controller =
        new GameController(GameState(), fileIO)

      controller.saveGame("save-as-json") shouldBe Success(())
      fileIO.savedPath shouldBe Some("saves/save-as-json.json")
    }
  }

  "GameController.loadGame" should {

    "replace the state, notify observers and choose PlacingPhase when stones remain in hand" in {
      val loadedState =
        stateWith(
          stones = Seq(Position(0, 0) -> PlayerId.One),
          playerOneStonesInHand = 8,
          playerTwoStonesInHand = 9,
          currentPlayer = PlayerId.Two
        )

      val fileIO =
        new RecordingFileIO(Success(loadedState))

      val controller =
        new GameController(GameState(), fileIO)

      val observer =
        new RecordingObserver

      controller.addObserver(observer)

      controller.loadGame("loaded.xml") shouldBe Success(())
      fileIO.loadedPath shouldBe Some("saves/loaded.xml")
      observer.updateCalls shouldBe 1
      controller.currentPrompt shouldBe
        "Player 2 enter position (e.g. a1): "
    }

    "choose MovingPhase when a loaded game has no stones in hand" in {
      val loadedState =
        stateWith(
          stones = Seq(
            Position(0, 0) -> PlayerId.One,
            Position(0, 3) -> PlayerId.One,
            Position(0, 5) -> PlayerId.One,
            Position(1, 0) -> PlayerId.Two,
            Position(1, 3) -> PlayerId.Two,
            Position(1, 5) -> PlayerId.Two
          ),
          playerOneStonesInHand = 0,
          playerTwoStonesInHand = 0,
          currentPlayer = PlayerId.One
        )

      val controller =
        new GameController(
          GameState(),
          new RecordingFileIO(Success(loadedState))
        )

      controller.loadGame("moving.xml") shouldBe Success(())
      controller.currentPrompt shouldBe
        "Player 1 move: start target (e.g. a1 d1): "
    }

    "return Failure and not notify observers when loading fails" in {
      val fileIO =
        new RecordingFileIO(Failure(new RuntimeException("missing save")))

      val controller =
        new GameController(GameState(), fileIO)

      val observer =
        new RecordingObserver

      controller.addObserver(observer)

      val result =
        controller.loadGame("missing.xml")

      result.isFailure shouldBe true
      result.failed.get.getMessage shouldBe "missing save"
      observer.updateCalls shouldBe 0
    }
  }

  "GameController" should {

    "start directly in MovingPhase when the supplied initial state has no stones in hand" in {
      val movingState =
        stateWith(
          stones = Seq(
            Position(0, 0) -> PlayerId.One,
            Position(0, 3) -> PlayerId.One,
            Position(0, 5) -> PlayerId.One,
            Position(1, 0) -> PlayerId.Two,
            Position(1, 3) -> PlayerId.Two,
            Position(1, 5) -> PlayerId.Two
          ),
          playerOneStonesInHand = 0,
          playerTwoStonesInHand = 0,
          currentPlayer = PlayerId.One
        )

      val controller =
        new GameController(
          movingState,
          new RecordingFileIO(Success(movingState))
        )

      controller.currentPrompt shouldBe
        "Player 1 move: start target (e.g. a1 d1): "
    }

    "keep the player in RemovingPhase after forming a mill while placing" in {
      val placingMillState =
        stateWith(
          stones = Seq(
            Position(0, 0) -> PlayerId.One,
            Position(0, 1) -> PlayerId.One,
            Position(1, 0) -> PlayerId.Two
          ),
          playerOneStonesInHand = 7,
          playerTwoStonesInHand = 8,
          currentPlayer = PlayerId.One
        )

      val controller =
        new GameController(
          placingMillState,
          new RecordingFileIO(Success(placingMillState))
        )

      controller.handleInput("g1") shouldBe Success(())
      controller.currentPrompt shouldBe
        "Player 1 remove an opponent stone: "

      controller.handleInput("b2") shouldBe Success(())
      controller.currentPrompt shouldBe
        "Player 2 enter position (e.g. a1): "
    }

    "declare the game over after removal leaves the opponent with two stones" in {
      val gameBeforeWinningRemoval =
        stateWith(
          stones = Seq(
            Position(0, 0) -> PlayerId.One,
            Position(0, 1) -> PlayerId.One,
            Position(1, 0) -> PlayerId.Two,
            Position(1, 1) -> PlayerId.Two,
            Position(1, 2) -> PlayerId.Two
          ),
          playerOneStonesInHand = 1,
          playerTwoStonesInHand = 0,
          currentPlayer = PlayerId.One
        )

      val controller =
        new GameController(
          gameBeforeWinningRemoval,
          new RecordingFileIO(Success(gameBeforeWinningRemoval))
        )

      controller.handleInput("g1") shouldBe Success(())
      controller.handleInput("b2") shouldBe Success(())

      controller.isGameOver shouldBe true
      controller.boardViewModel.nextPlayerNumber shouldBe 2
    }

    "return to RemovingPhase when undoing an opponent-stone removal" in {
      val placingMillState =
        stateWith(
          stones = Seq(
            Position(0, 0) -> PlayerId.One,
            Position(0, 1) -> PlayerId.One,
            Position(1, 0) -> PlayerId.Two
          ),
          playerOneStonesInHand = 7,
          playerTwoStonesInHand = 8,
          currentPlayer = PlayerId.One
        )

      val controller =
        new GameController(
          placingMillState,
          new RecordingFileIO(Success(placingMillState))
        )

      controller.handleInput("g1") shouldBe Success(())
      controller.handleInput("b2") shouldBe Success(())

      controller.undo() shouldBe Success(())
      controller.currentPrompt shouldBe
        "Player 1 remove an opponent stone: "
      controller.boardViewModel.stones.exists(
        _.playerNumber == 2
      ) shouldBe true
    }
  }
