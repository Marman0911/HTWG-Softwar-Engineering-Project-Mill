package controller

import model.game.GameState

trait GamePhase:
  def handleInput(input: String, state: GameState): Either[String, GameCommand]
  def prompt(state: GameState): String
  def next(state: GameState): GamePhase