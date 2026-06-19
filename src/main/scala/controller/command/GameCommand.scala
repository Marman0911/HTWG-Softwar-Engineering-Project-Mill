package controller.command

import model.game.GameState
import scala.util.Try

trait GameCommand:
  def execute(state: GameState): Try[GameState]
  def undo(state: GameState): Try[GameState]