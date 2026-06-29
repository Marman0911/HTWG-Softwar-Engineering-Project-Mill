package controller

import controller.command.{GameCommand, RemoveCommand}
import model.board.Board
import model.game.{GameState, MillRules}
import model.player.PlayerId

import scala.util.{Failure, Success, Try}

/**
 * Nach einer gebildeten Mühle muss der aktuelle Spieler
 * einen erlaubten gegnerischen Stein entfernen.
 */
class RemovingPhase(parsePos: (String, Board) => Option[model.board.Position]) extends GamePhase:

  def handleInput(input: String, state: GameState): Try[GameCommand] =
    parsePos(input, state.board) match
      case None =>
        Failure(new IllegalArgumentException("Invalid removal position."))

      case Some(pos) =>
        val removablePositions =
          MillRules.removableOpponentPositions(
            state.board,
            state.currentPlayer
          )

        if removablePositions.contains(pos) then
          Success(RemoveCommand(pos))
        else
          Failure(
            new IllegalArgumentException(
              "Invalid removal. Choose a removable opponent stone."
            )
          )

  def prompt(state: GameState): String =
    val playerNumber =
      if state.currentPlayer == PlayerId.One then 1 else 2

    s"Player $playerNumber remove an opponent stone: "

  def next(state: GameState): GamePhase =
    val allStonesPlaced =
      state.player1.stonesInHand == 0 &&
        state.player2.stonesInHand == 0

    if allStonesPlaced then
      new MovingPhase(parsePos)
    else
      new PlacingPhase(parsePos)
