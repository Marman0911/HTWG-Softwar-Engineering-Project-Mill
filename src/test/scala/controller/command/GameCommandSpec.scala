package controller.command

import model.board.Position
import model.game.GameState
import model.player.PlayerId
import org.scalatest.flatspec.AnyFlatSpec
import org.scalatest.matchers.should.Matchers
import scala.util.{Try, Success, Failure}

class GameCommandSpec extends AnyFlatSpec with Matchers:

  class AlwaysSuccessCommand extends GameCommand:
    def execute(state: GameState): Try[GameState] = Success(state)
    def undo(state: GameState): Try[GameState]    = Success(state)

  class AlwaysFailCommand extends GameCommand:
    def execute(state: GameState): Try[GameState] = Failure(new RuntimeException("Command failed"))
    def undo(state: GameState): Try[GameState]    = Success(state)

  val state: GameState = GameState()

  "A GameCommand" should "return Success(GameState) on success" in:
    AlwaysSuccessCommand().execute(state).isSuccess shouldBe true

  it should "return Failure on failure" in:
    AlwaysFailCommand().execute(state).isFailure shouldBe true

  it should "not modify state when returning Failure" in:
    AlwaysFailCommand().execute(state).isFailure shouldBe true

  it should "return a GameState when successful" in:
    AlwaysSuccessCommand().execute(state) shouldBe Success(state)

  // PlaceCommand.undo — fehlender Branch
  "PlaceCommand.undo" should "restore state after placing a stone" in:
    val pos      = Position(0, 0)
    val afterPlace = PlaceCommand(pos).execute(state).get
    val undoResult = PlaceCommand(pos).undo(afterPlace)
    undoResult.isSuccess shouldBe true
    undoResult.get.board.placedStones.get(pos) shouldBe None

  it should "return Failure when undoing on empty position" in:
    val pos    = Position(0, 0)
    val result = PlaceCommand(pos).undo(state)  // nichts platziert → removeStone gibt None
    result.isFailure shouldBe true
    result.failed.get.getMessage shouldBe "Konnte Stein zum Rückgängigmachen nicht entfernen!"

  // MoveCommand
  "MoveCommand" should "move a stone successfully" in:
    val from     = Position(0, 0)
    val to       = Position(0, 1)
    val afterP1  = PlaceCommand(from).execute(state).get
    val afterP2  = PlaceCommand(Position(1, 0)).execute(afterP1).get
    val result   = MoveCommand(from, to).execute(afterP2)
    result.isSuccess shouldBe true
    result.get.board.placedStones.get(to) shouldBe Some(PlayerId.One)

  it should "return Failure for invalid move" in:
    val result = MoveCommand(Position(0, 0), Position(0, 1)).execute(state)
    result.isFailure shouldBe true

  it should "undo a move successfully" in:
    val from     = Position(0, 0)
    val to       = Position(0, 1)
    val afterP1  = PlaceCommand(from).execute(state).get
    val afterP2  = PlaceCommand(Position(1, 0)).execute(afterP1).get
    val afterMove = MoveCommand(from, to).execute(afterP2).get
    val undoResult = MoveCommand(from, to).undo(afterMove)
    undoResult.isSuccess shouldBe true
    undoResult.get.board.placedStones.get(from) shouldBe Some(PlayerId.One)

  it should "return Failure when undo move is invalid" in:
    val result = MoveCommand(Position(0, 0), Position(0, 1)).undo(state)
    result.isFailure shouldBe true