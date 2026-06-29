package controller.command

import model.board.Position
import model.game.{GameState, MillRules}

import scala.util.{Failure, Success, Try}

/**
 * Entfernt einen erlaubten gegnerischen Stein nach einer Mühle.
 */
case class RemoveCommand(pos: Position) extends GameCommand:

  override def execute(state: GameState): Try[GameState] =
    val removablePositions =
      MillRules.removableOpponentPositions(
        state.board,
        state.currentPlayer
      )

    if !removablePositions.contains(pos) then
      Failure(
        new IllegalArgumentException(
          "Invalid removal. Choose a removable opponent stone."
        )
      )
    else
      state.removeOpponentStone(pos) match
        case Some(newState) =>
          Success(newState)

        case None =>
          Failure(
            new IllegalArgumentException(
              "The selected opponent stone cannot be removed."
            )
          )

  override def undo(state: GameState): Try[GameState] =
    state.restoreOpponentStone(pos) match
      case Some(previousState) =>
        Success(previousState)

      case None =>
        Failure(
          new IllegalStateException(
            "Konnte den entfernten Stein nicht wiederherstellen."
          )
        )
