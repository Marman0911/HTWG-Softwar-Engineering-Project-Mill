package controller

import controller.command.*
import model.board.Board
import model.board.Position
import model.game.GameState

import scala.util.{Failure, Success, Try}

class PlacingPhase(parsePos: (String, Board) => Option[Position]) extends GamePhase:

  def handleInput(input: String, state: GameState): Try[GameCommand] =
    parsePos(input, state.board) match
      case None =>
        Failure(new IllegalArgumentException(GameMessages.invalidPosition))

      case Some(pos) =>
        Success(PlaceCommand(pos))

  def prompt(state: GameState): String =
    GameMessages.promptFor(state.currentPlayer)

  def next(state: GameState): GamePhase =
    val allStonesPlaced =
      state.player1.stonesInHand == 0 &&
        state.player2.stonesInHand == 0

    if allStonesPlaced then
      new MovingPhase(parsePos)
    else
      this