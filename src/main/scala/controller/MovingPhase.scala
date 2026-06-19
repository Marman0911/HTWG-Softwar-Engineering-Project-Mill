package controller

import model.game.GameState
import model.board.Board
import model.board.Position

class MovingPhase(parsePos: (String, Board) => Option[Position]) extends GamePhase:

  def handleInput(input: String, state: GameState): Either[String, GameCommand] =
    Left("Moving phase: not yet implemented")

  def prompt(state: GameState): String =
    "[Moving] Enter move (from to): "

  def next(state: GameState): GamePhase = this