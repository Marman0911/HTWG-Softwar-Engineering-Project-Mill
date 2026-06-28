package model.fileio

import model.game.GameState
import scala.util.Try

trait FileIOInterface:
  def save(state: GameState, filePath: String): Unit
  def load(filePath: String): Try[GameState]