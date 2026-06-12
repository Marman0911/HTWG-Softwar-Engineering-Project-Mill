package controller.command

import model.GameState

trait GameCommand:
  def execute(state: GameState): Option[GameState]