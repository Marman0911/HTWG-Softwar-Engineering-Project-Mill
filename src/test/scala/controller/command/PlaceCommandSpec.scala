package controller.command

import org.scalatest.flatspec.AnyFlatSpec
import org.scalatest.matchers.should.Matchers
import model.{GameState, Position}

class PlaceCommandSpec extends AnyFlatSpec with Matchers:

  val state    = GameState()
  val validPos = Position(0, 0)
  val otherPos = Position(0, 1)

  "A PlaceCommand" should "return Some(GameState) when position is empty" in:
    val cmd = PlaceCommand(validPos)
    cmd.execute(state) shouldBe defined

  it should "return None when position is already occupied" in:
    val occupiedState = PlaceCommand(validPos).execute(state).get
    val cmd           = PlaceCommand(validPos)
    cmd.execute(occupiedState) shouldBe None

  it should "place a stone on the board" in:
    val cmd      = PlaceCommand(validPos)
    val newState = cmd.execute(state).get
    newState.board.stones(validPos) shouldBe defined

  it should "not modify the original state (immutability)" in:
    val cmd = PlaceCommand(validPos)
    cmd.execute(state)
    state.board.stones(validPos) shouldBe None

  it should "switch to the next player after placing" in:
    val cmd      = PlaceCommand(validPos)
    val newState = cmd.execute(state).get
    newState.currentPlayer should not equal state.currentPlayer

  it should "allow placing on two different positions" in:
    val first  = PlaceCommand(validPos).execute(state).get
    val second = PlaceCommand(otherPos).execute(first)
    second shouldBe defined