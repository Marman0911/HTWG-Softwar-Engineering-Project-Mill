package controller.command

import controller.GameMessages
import model.board.Position
import model.game.GameState

import scala.util.{Failure, Success, Try}

case class MoveCommand(from: Position, to: Position) extends GameCommand:

  override def execute(state: GameState): Try[GameState] =
    state.moveStone(from, to) match
      case Some(newState) =>
        Success(newState)

      case None =>
        Failure(new IllegalArgumentException(GameMessages.invalidMove))

  override def undo(state: GameState): Try[GameState] =
    state.undoMoveStone(from, to) match
      case Some(previousState) =>
        Success(previousState)

      case None =>
        Failure(new IllegalStateException("Konnte den Bewegungszug nicht rückgängig machen."))
