package controller

import model.GameState

trait GamePhase:
  def handleInput(input: String, state: GameState): Either[String, GameState]
  def prompt(state: GameState): String
  def next(state: GameState): GamePhase