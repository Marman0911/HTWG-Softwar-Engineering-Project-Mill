package model.game.impl

import model.board.Board
import model.board.Position
import model.game.GameState
import model.player.Player
import model.player.PlayerId

private[game] case class GameStateImpl(
    board: Board,
    player1: Player,
    player2: Player,
    currentPlayer: PlayerId = PlayerId.One
) extends GameState:

  def currentPlayerObj: Player =
    if currentPlayer == PlayerId.One then player1 else player2

  private def nextPlayer: PlayerId =
    if currentPlayer == PlayerId.One then PlayerId.Two else PlayerId.One

  def placeStone(pos: Position): Option[GameState] =
    board.placeStone(pos, currentPlayer).map: newBoard =>
      copy(
        board = newBoard,
        currentPlayer = nextPlayer
      )

  def removeStone(pos: Position): Option[GameState] =
      board.removeStone(pos).map: newBoard =>
        copy(
          board = newBoard,
          currentPlayer = nextPlayer // Wechselt wieder zurück zum Spieler davor
        )