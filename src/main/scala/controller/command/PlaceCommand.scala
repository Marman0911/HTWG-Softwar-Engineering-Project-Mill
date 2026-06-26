// WICHTIG: Das sorgt dafür, dass PlaceCommand das GameState-Trait überhaupt kennt!
package controller.command

import controller.GameMessages
import model.board.Position
import model.game.GameState

import scala.util.{Failure, Success, Try}

case class PlaceCommand(pos: Position) extends GameCommand:

  override def execute(state: GameState): Try[GameState] =
    state.placeStone(pos) match
      case Some(newState) =>
        Success(newState)

      case None =>
        Failure(new Exception(GameMessages.occupiedPosition))

  override def undo(state: GameState): Try[GameState] =
    state.removeStone(pos) match
      case Some(newState) =>
        Success(newState)

      case None =>
        Failure(new Exception("Konnte Stein zum Rückgängigmachen nicht entfernen!"))