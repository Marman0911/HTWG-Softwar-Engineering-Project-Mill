package controller

import model.game.GameState
import model.board.Board
import model.board.Position
import scala.util.Try

trait IController extends Observable:
  def isGameOver: Boolean
  def boardViewModel: BoardViewModel
  def currentPrompt: String
  def handleInput(input: String): Try[Unit]
  def welcomeMessage: String
  def undo(): Try[Unit]