package controller.command

import model.game.GameState

trait GameCommand:
  def execute(state: GameState): Option[GameState]