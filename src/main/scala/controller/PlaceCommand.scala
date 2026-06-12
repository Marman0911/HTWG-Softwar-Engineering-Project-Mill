package controller.command
import model.{GameState, Position}
import controller.command.GameCommand

case class PlaceCommand(pos: Position) extends GameCommand:
  def execute(state: GameState): Option[GameState] =
    state.placeStone(pos)