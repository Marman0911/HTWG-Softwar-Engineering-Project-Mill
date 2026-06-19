package controller

import model.game.GameState
import controller.command.GameCommand
import scala.util.Try

trait GamePhase:
  def handleInput(input: String, state: GameState): Try[GameCommand]
  def prompt(state: GameState): String
  def next(state: GameState): GamePhase