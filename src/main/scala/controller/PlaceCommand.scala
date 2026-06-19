package controller.command

import model.game.GameState
import model.board.Board
import model.board.Position

case class PlaceCommand(pos: Position) extends GameCommand:
  def execute(state: GameState): Option[GameState] =
    state.placeStone(pos)