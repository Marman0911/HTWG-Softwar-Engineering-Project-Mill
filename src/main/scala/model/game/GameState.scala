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

  // Wird nur für Undo eines gesetzten Steins benutzt.
  def removeStone(pos: Position): Option[GameState]

  // Entfernt nach einer Mühle einen gegnerischen Stein.
  // Der entfernte Stein kommt nicht zurück in die Reserve.
  def removeOpponentStone(pos: Position): Option[GameState]

  // Wird nur für Undo eines entfernten gegnerischen Steins benutzt.
  def restoreOpponentStone(pos: Position): Option[GameState]

  // Bewegt den Stein des aktuellen Spielers.
  def moveStone(from: Position, to: Position): Option[GameState]

  // Wird für Undo eines Bewegungszuges benutzt.
  def undoMoveStone(from: Position, to: Position): Option[GameState]

object GameState:

  def apply(): GameState =
    GameComponent.standard
