package controller

import model.{GameState, MillBoard, Position}
import controller.GameMessages
import controller.GamePhase
import controller.MovingPhase

class PlacingPhase(parsePos: (String, MillBoard) => Option[Position]) extends GamePhase:

  def handleInput(input: String, state: GameState): Either[String, GameState] =
    parsePos(input, state.board) match
      case None      => Left(GameMessages.invalidPosition)
      case Some(pos) => state.placeStone(pos).toRight(GameMessages.occupiedPosition)

  def prompt: String =
    "[Placing] Enter position (e.g. a1): "

  def next(state: GameState): GamePhase =
    val placed = state.board.stones.values.count(_.isDefined)
    val max    = state.board.boardSize * 8
    if placed < max then this else new MovingPhase(parsePos)