package controller

import model.game.GameState
import model.board.Board
import model.board.Position
import controller.command.*
import scala.util.{Try, Success, Failure}

class PlacingPhase(parsePos: (String, Board) => Option[Position]) extends GamePhase:

  def handleInput(input: String, state: GameState): Try[GameCommand] =
    parsePos(input, state.board) match
      case None      => Failure(new IllegalArgumentException(GameMessages.invalidPosition))
      case Some(pos) => Success(PlaceCommand(pos))

  def prompt(state: GameState): String =
    GameMessages.promptFor(state.currentPlayer)

  def next(state: GameState): GamePhase =
    val placed = state.board.occupiedCount
    val max    = state.board.boardSize * 8
    if placed < max then this else new MovingPhase(parsePos)