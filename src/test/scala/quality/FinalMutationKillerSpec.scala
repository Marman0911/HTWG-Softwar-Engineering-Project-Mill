package quality

import java.io.{ByteArrayOutputStream, File, PrintStream}
import java.util.UUID

import controller.{GameController, RemovingPhase}
import controller.command.{MoveCommand, RemoveCommand}
import model.board.{Board, BoardComponent, Position}
import model.fileio.{FileIOInterface, JsonFileIO, XmlFileIO}
import model.game.{GameComponent, GameState, MillRules}
import model.player.{Player, PlayerComponent, PlayerId}
import org.scalatest.matchers.should.Matchers
import org.scalatest.wordspec.AnyWordSpec

import scala.util.{Failure, Try}

/**
 * Small, targeted tests for boundary cases that mutation testing found.
 * They test real behaviour; no production package is excluded here.
 */
class FinalMutationKillerSpec extends AnyWordSpec with Matchers:

  private def boardWith(stones: Seq[(Position, PlayerId)]): Board =
    stones.foldLeft(BoardComponent.create(3)): (board, stone) =>
      board.placeStone(stone._1, stone._2).get

  private def stateWith(
      stones: Seq[(Position, PlayerId)],
      playerOneStonesInHand: Int = 0,
      playerTwoStonesInHand: Int = 0,
      currentPlayer: PlayerId = PlayerId.One
  ): GameState =
    val board = boardWith(stones)

    GameComponent.create(
      board,
      PlayerComponent.create(
        PlayerId.One,
        stonesInHand = playerOneStonesInHand,
        stonesOnBoard = stones.count(_._2 == PlayerId.One)
      ),
      PlayerComponent.create(
        PlayerId.Two,
        stonesInHand = playerTwoStonesInHand,
        stonesOnBoard = stones.count(_._2 == PlayerId.Two)
      ),
      currentPlayer
    )

  private def asymmetricState(currentPlayer: PlayerId): GameState =
    stateWith(
      Seq(
        Position(0, 0) -> PlayerId.One,
        Position(1, 0) -> PlayerId.Two,
        Position(1, 1) -> PlayerId.Two
      ),
      playerOneStonesInHand = 8,
      playerTwoStonesInHand = 7,
      currentPlayer = currentPlayer
    )

  private def captureOut(action: => Unit): String =
    val bytes = new ByteArrayOutputStream()
    val stream = new PrintStream(bytes, true, "UTF-8")

    Console.withOut(stream):
      action

    stream.close()
    bytes.toString("UTF-8")

  private final class FailingFileIO extends FileIOInterface:
    def save(state: GameState, filePath: String): Unit = ()

    def load(filePath: String): Try[GameState] =
      Failure(new RuntimeException("missing save"))

  private final class RemovalFailsState extends GameState:
    val board: Board =
      boardWith(Seq(Position(0, 0) -> PlayerId.Two))

    val player1: Player =
      PlayerComponent.create(PlayerId.One, stonesInHand = 0, stonesOnBoard = 3)

    val player2: Player =
      PlayerComponent.create(PlayerId.Two, stonesInHand = 0, stonesOnBoard = 3)

    val currentPlayer: PlayerId = PlayerId.One

    def currentPlayerObj: Player = player1

    def placeStone(pos: Position): Option[GameState] = None
    def removeStone(pos: Position): Option[GameState] = None
    def removeOpponentStone(pos: Position): Option[GameState] = None
    def restoreOpponentStone(pos: Position): Option[GameState] = None
    def moveStone(from: Position, to: Position): Option[GameState] = None
    def undoMoveStone(from: Position, to: Position): Option[GameState] = None

  "MillBoard.neighbours" should {

    "respect inner and outer ring boundaries and never connect even slots across rings" in {
      val board = BoardComponent.create(3)

      board.neighbours(Position(0, 1)) shouldBe Seq(
        Position(0, 0),
        Position(0, 2),
        Position(1, 1)
      )

      board.neighbours(Position(1, 0)) shouldBe Seq(
        Position(1, 7),
        Position(1, 1)
      )

      board.neighbours(Position(2, 1)) shouldBe Seq(
        Position(2, 0),
        Position(2, 2),
        Position(1, 1)
      )
    }
  }

  "JsonFileIO" should {

    "save without a parent directory and preserve a player-one turn with unequal stone counts" in {
      val file =
        new File(s"mutation-json-${UUID.randomUUID()}.json")

      try
        val fileIO = new JsonFileIO
        val original = asymmetricState(PlayerId.One)

        val output = captureOut:
          fileIO.save(original, file.getPath)

        file.exists shouldBe true
        output should include(s"Erfolgreich als JSON gespeichert unter: ${file.getPath}")

        val loaded = fileIO.load(file.getPath).get
        loaded.currentPlayer shouldBe PlayerId.One
        loaded.player1.stonesOnBoard shouldBe 1
        loaded.player2.stonesOnBoard shouldBe 2
      finally
        file.delete()
    }
  }

  "XmlFileIO" should {

    "save without a parent directory and preserve a player-one turn with unequal stone counts" in {
      val file =
        new File(s"mutation-xml-${UUID.randomUUID()}.xml")

      try
        val fileIO = new XmlFileIO
        val original = asymmetricState(PlayerId.One)

        val output = captureOut:
          fileIO.save(original, file.getPath)

        file.exists shouldBe true
        output should include(s"Erfolgreich als XML gespeichert unter: ${file.getPath}")

        val loaded = fileIO.load(file.getPath).get
        loaded.currentPlayer shouldBe PlayerId.One
        loaded.player1.stonesOnBoard shouldBe 1
        loaded.player2.stonesOnBoard shouldBe 2
      finally
        file.delete()
    }
  }

  "MoveCommand.undo" should {

    "keep its explanatory failure message when there is no moved stone to undo" in {
      val result =
        MoveCommand(Position(0, 0), Position(0, 1)).undo(GameState())

      result.isFailure shouldBe true
      result.failed.get.getMessage shouldBe
        "Konnte den Bewegungszug nicht rückgängig machen."
    }
  }

  "GameState" should {

    "not place a stone for an invalid negative reserve count" in {
      val state =
        GameComponent.create(
          BoardComponent.create(3),
          PlayerComponent.create(PlayerId.One, stonesInHand = -1, stonesOnBoard = 0),
          PlayerComponent.create(PlayerId.Two, stonesInHand = 9, stonesOnBoard = 0),
          PlayerId.One
        )

      state.placeStone(Position(0, 0)) shouldBe None
    }

    "restore the correct player data when undoing player two's placed stone" in {
      val beforeUndo =
        stateWith(
          Seq(
            Position(0, 0) -> PlayerId.One,
            Position(1, 0) -> PlayerId.Two
          ),
          playerOneStonesInHand = 8,
          playerTwoStonesInHand = 8,
          currentPlayer = PlayerId.One
        )

      val afterUndo =
        beforeUndo.removeStone(Position(1, 0)).get

      afterUndo.player1.id shouldBe PlayerId.One
      afterUndo.player1.stonesInHand shouldBe 8
      afterUndo.player2.id shouldBe PlayerId.Two
      afterUndo.player2.stonesInHand shouldBe 9
      afterUndo.player2.stonesOnBoard shouldBe 0
    }

    "remove player one's stone without replacing player one's data by player two's data" in {
      val target = Position(0, 0)
      val state =
        stateWith(
          Seq(
            target -> PlayerId.One,
            Position(1, 0) -> PlayerId.Two
          ),
          currentPlayer = PlayerId.Two
        )

      val afterRemoval =
        state.removeOpponentStone(target).get

      afterRemoval.player1.id shouldBe PlayerId.One
      afterRemoval.player1.stonesOnBoard shouldBe 0
      afterRemoval.player2.id shouldBe PlayerId.Two
      afterRemoval.player2.stonesOnBoard shouldBe 1
    }

    "remove player two's stone without replacing player two's data by player one's data" in {
      val target = Position(0, 0)
      val state =
        stateWith(
          Seq(
            target -> PlayerId.Two,
            Position(1, 0) -> PlayerId.One
          ),
          currentPlayer = PlayerId.One
        )

      val afterRemoval =
        state.removeOpponentStone(target).get

      afterRemoval.player1.id shouldBe PlayerId.One
      afterRemoval.player1.stonesOnBoard shouldBe 1
      afterRemoval.player2.id shouldBe PlayerId.Two
      afterRemoval.player2.stonesOnBoard shouldBe 0
    }
  }

  "RemoveCommand" should {

    "report the concrete failure when the state refuses an otherwise removable stone" in {
      val result =
        RemoveCommand(Position(0, 0)).execute(new RemovalFailsState)

      result.isFailure shouldBe true
      result.failed.get.getMessage shouldBe
        "The selected opponent stone cannot be removed."
    }

    "report the concrete failure when an opponent stone cannot be restored" in {
      val result =
        RemoveCommand(Position(0, 0)).undo(new RemovalFailsState)

      result.isFailure shouldBe true
      result.failed.get.getMessage shouldBe
        "Konnte den entfernten Stein nicht wiederherstellen."
    }
  }

  "MillRules.allMills" should {

    "have no cross-ring mill on a one-ring board" in {
      MillRules.allMills(BoardComponent.create(1)).size shouldBe 4
    }
  }

  "GameController" should {

    "choose PlacingPhase when only player one still has stones in hand" in {
      val state =
        stateWith(
          Seq(
            Position(0, 0) -> PlayerId.One,
            Position(0, 3) -> PlayerId.One,
            Position(1, 0) -> PlayerId.Two,
            Position(1, 3) -> PlayerId.Two,
            Position(1, 5) -> PlayerId.Two
          ),
          playerOneStonesInHand = 1,
          playerTwoStonesInHand = 0,
          currentPlayer = PlayerId.One
        )

      new GameController(state, new FailingFileIO).currentPrompt shouldBe
        "Player 1 enter position (e.g. a1): "
    }

    "choose PlacingPhase when only player two still has stones in hand" in {
      val state =
        stateWith(
          Seq(
            Position(0, 0) -> PlayerId.One,
            Position(0, 3) -> PlayerId.One,
            Position(0, 5) -> PlayerId.One,
            Position(1, 0) -> PlayerId.Two,
            Position(1, 3) -> PlayerId.Two
          ),
          playerOneStonesInHand = 0,
          playerTwoStonesInHand = 1,
          currentPlayer = PlayerId.Two
        )

      new GameController(state, new FailingFileIO).currentPrompt shouldBe
        "Player 2 enter position (e.g. a1): "
    }

    "write the load error message to the configured error stream" in {
      val controller =
        new GameController(GameState(), new FailingFileIO)

      val bytes = new ByteArrayOutputStream()
      val stream = new PrintStream(bytes, true, "UTF-8")

      val result =
        Console.withErr(stream):
          controller.loadGame("missing.xml")

      stream.close()

      result.isFailure shouldBe true
      bytes.toString("UTF-8") should include(
        "Fehler beim Laden des Spielstands: missing save"
      )
    }
  }

  "RemovingPhase.prompt" should {

    "show player two when player two must remove a stone" in {
      val state =
        stateWith(
          Seq(Position(0, 0) -> PlayerId.One),
          currentPlayer = PlayerId.Two
        )

      val parseNothing: (String, Board) => Option[Position] =
        (_, _) => None

      RemovingPhase(parseNothing).prompt(state) shouldBe
        "Player 2 remove an opponent stone: "
    }
  }
