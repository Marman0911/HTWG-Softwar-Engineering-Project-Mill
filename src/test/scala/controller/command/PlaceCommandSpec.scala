package controller.command

import model.board.Position
import model.game.GameState
import model.player.PlayerId
import org.scalatest.flatspec.AnyFlatSpec
import org.scalatest.matchers.should.Matchers
import scala.util.{Failure, Success}

class PlaceCommandSpec extends AnyFlatSpec with Matchers:

  val state: GameState =
    GameState()

  val validPos: Position =
    Position(0, 0)

  val otherPos: Position =
    Position(0, 1)

  "PlaceCommand" should "place a stone on an empty position" in:
    val cmd = PlaceCommand(validPos)

    val result = cmd.execute(state)

    result.isSuccess shouldBe true

    val newState = result.get

    newState.board.placedStones.get(validPos) shouldBe Some(PlayerId.One)

  it should "not modify the original state" in:
    val cmd = PlaceCommand(validPos)

    cmd.execute(state)

    state.board.placedStones.get(validPos) shouldBe None

  it should "switch the current player after placing" in:
    val cmd = PlaceCommand(validPos)

    val result = cmd.execute(state)

    result.isSuccess shouldBe true

    val newState = result.get

    newState.currentPlayer should not equal state.currentPlayer

  it should "return Failure when placing on an occupied position" in:
    val occupiedState = PlaceCommand(validPos).execute(state).get

    val cmd = PlaceCommand(validPos)

    cmd.execute(occupiedState) match {
      case Failure(e) => e.getMessage shouldBe "Position occupied."
      case Success(_) => fail("Test fehlgeschlagen: Zug auf besetztes Feld hätte scheitern müssen!")
    }

  it should "allow placing on another empty position" in:
    val occupiedState = PlaceCommand(validPos).execute(state).get

    val cmd = PlaceCommand(otherPos)

    cmd.execute(occupiedState).isSuccess shouldBe true