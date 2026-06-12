package controller.command
import model.{GameState, Position}

case class PlaceCommand(pos: Position) extends GameCommand:
  def execute(state: GameState): Option[GameState] =
    state.placeStone(pos)