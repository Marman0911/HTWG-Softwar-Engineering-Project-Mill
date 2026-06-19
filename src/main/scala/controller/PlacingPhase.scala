package controller

import model.game.GameState
import model.board.Board
import model.board.Position

class PlacingPhase(parsePos: (String, Board) => Option[Position]) extends GamePhase:

  def handleInput(input: String, state: GameState): Either[String, GameCommand] =
    parsePos(input, state.board) match
      case None      => Left(GameMessages.invalidPosition)
      case Some(pos) => Right(PlaceCommand(pos))

  def prompt(state: GameState): String =
    GameMessages.promptFor(state.currentPlayer)

  def next(state: GameState): GamePhase =
    val placed = state.board.occupiedCount
    val max    = state.board.boardSize * 8
    if placed < max then this else new MovingPhase(parsePos)