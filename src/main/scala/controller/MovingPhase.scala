package controller

import model.{GameState, MillBoard, Position}
import controller.GamePhase

class MovingPhase(parsePos: (String, MillBoard) => Option[Position]) extends GamePhase:

  def handleInput(input: String, state: GameState): Either[String, GameState] =
    Left("Moving phase: not yet implemented")

  def prompt: String =
    "[Moving] Enter move (from to): "

  def next(state: GameState): GamePhase = this