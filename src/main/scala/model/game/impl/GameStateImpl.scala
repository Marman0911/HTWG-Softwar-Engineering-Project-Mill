package model.game.impl

import model.board.Board
import model.board.Position
import model.game.GameState
import model.player.Player
import model.player.PlayerComponent
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

  private def previousPlayer: PlayerId =
    if currentPlayer == PlayerId.One then PlayerId.Two else PlayerId.One

  private def playerAfterPlacingStone(player: Player): Player =
    PlayerComponent.create(
      id = player.id,
      stonesInHand = player.stonesInHand - 1,
      stonesOnBoard = player.stonesOnBoard + 1
    )

  private def playerAfterUndoingPlacement(player: Player): Player =
    PlayerComponent.create(
      id = player.id,
      stonesInHand = player.stonesInHand + 1,
      stonesOnBoard = player.stonesOnBoard - 1
    )

  def placeStone(pos: Position): Option[GameState] =
    if currentPlayerObj.stonesInHand <= 0 then None
    else
      board.placeStone(pos, currentPlayer).map: newBoard =>
        val updatedPlayer =
          playerAfterPlacingStone(currentPlayerObj)

        currentPlayer match
          case PlayerId.One =>
            copy(
              board = newBoard,
              player1 = updatedPlayer,
              currentPlayer = nextPlayer
            )

          case PlayerId.Two =>
            copy(
              board = newBoard,
              player2 = updatedPlayer,
              currentPlayer = nextPlayer
            )

  def removeStone(pos: Position): Option[GameState] =
    board.placedStones.get(pos).flatMap: owner =>
      board.removeStone(pos).map: newBoard =>
        val playerBeforeUndo =
          if owner == PlayerId.One then player1 else player2

        val restoredPlayer =
          playerAfterUndoingPlacement(playerBeforeUndo)

        owner match
          case PlayerId.One =>
            copy(
              board = newBoard,
              player1 = restoredPlayer,
              currentPlayer = PlayerId.One
            )

          case PlayerId.Two =>
            copy(
              board = newBoard,
              player2 = restoredPlayer,
              currentPlayer = PlayerId.Two
            )

  def moveStone(from: Position, to: Position): Option[GameState] =
    val startContainsCurrentPlayer =
      board.placedStones.get(from).contains(currentPlayer)

    val targetIsFree =
      !board.placedStones.contains(to)

    val mayUseAnyFreePosition =
      currentPlayerObj.canFly

    val targetIsNeighbour =
      board.neighbours(from).contains(to)

    val moveIsAllowed =
      mayUseAnyFreePosition || targetIsNeighbour

    if !startContainsCurrentPlayer || !targetIsFree || !moveIsAllowed then None
    else
      board.moveStone(from, to, currentPlayer).map: newBoard =>
        copy(
          board = newBoard,
          currentPlayer = nextPlayer
        )

  def undoMoveStone(from: Position, to: Position): Option[GameState] =
    val playerWhoMoved =
      previousPlayer

    board.moveStone(to, from, playerWhoMoved).map: newBoard =>
      copy(
        board = newBoard,
        currentPlayer = playerWhoMoved
      )
