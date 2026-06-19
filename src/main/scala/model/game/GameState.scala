package model.game

import model.board.Board
import model.board.Position
import model.player.Player
import model.player.PlayerId

trait GameState:

  def board: Board

  def player1: Player

  def player2: Player

  def currentPlayer: PlayerId

  def currentPlayerObj: Player

  def placeStone(pos: Position): Option[GameState]

  // HIER: Das brauchen wir zwingend für das Undo-System!
  def removeStone(pos: Position): Option[GameState] 


object GameState:

  def apply(): GameState =
    GameComponent.standard