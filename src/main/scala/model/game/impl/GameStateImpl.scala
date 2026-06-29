package model.game.impl

import model.board.Board
import model.board.Position
import model.game.{GameState, MillRules}
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

  private def playerAfterOpponentStoneRemoved(player: Player): Player =
    PlayerComponent.create(
      id = player.id,
      stonesInHand = player.stonesInHand,
      stonesOnBoard = player.stonesOnBoard - 1
    )

  private def playerAfterOpponentStoneRestored(player: Player): Player =
    PlayerComponent.create(
      id = player.id,
      stonesInHand = player.stonesInHand,
      stonesOnBoard = player.stonesOnBoard + 1
    )

  def placeStone(pos: Position): Option[GameState] =
    if currentPlayerObj.stonesInHand <= 0 then None
    else
      board.placeStone(pos, currentPlayer).map: newBoard =>
        val updatedPlayer =
          playerAfterPlacingStone(currentPlayerObj)

        // Wenn durch den gesetzten Stein eine Mühle entsteht,
        // bleibt derselbe Spieler am Zug, um einen Gegnerstein zu entfernen.
        val nextTurn =
          if MillRules.formsMillAt(newBoard, pos, currentPlayer) then
            currentPlayer
          else
            nextPlayer

        currentPlayer match
          case PlayerId.One =>
            copy(
              board = newBoard,
              player1 = updatedPlayer,
              currentPlayer = nextTurn
            )

          case PlayerId.Two =>
            copy(
              board = newBoard,
              player2 = updatedPlayer,
              currentPlayer = nextTurn
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

  def removeOpponentStone(pos: Position): Option[GameState] =
    // In der RemovingPhase ist currentPlayer der Spieler,
    // der gerade eine Mühle gebildet hat.
    val opponent =
      nextPlayer

    if !board.placedStones.get(pos).contains(opponent) then None
    else
      board.removeStone(pos).map: newBoard =>
        val updatedOpponent =
          playerAfterOpponentStoneRemoved(
            if opponent == PlayerId.One then player1 else player2
          )

        opponent match
          case PlayerId.One =>
            copy(
              board = newBoard,
              player1 = updatedOpponent,
              currentPlayer = opponent
            )

          case PlayerId.Two =>
            copy(
              board = newBoard,
              player2 = updatedOpponent,
              currentPlayer = opponent
            )

  def restoreOpponentStone(pos: Position): Option[GameState] =
    // Nach dem Entfernen ist currentPlayer der Spieler,
    // dessen Stein entfernt wurde.
    val removedPlayer =
      currentPlayer

    board.placeStone(pos, removedPlayer).map: newBoard =>
      val restoredPlayer =
        playerAfterOpponentStoneRestored(
          if removedPlayer == PlayerId.One then player1 else player2
        )

      removedPlayer match
        case PlayerId.One =>
          copy(
            board = newBoard,
            player1 = restoredPlayer,
            currentPlayer = previousPlayer
          )

        case PlayerId.Two =>
          copy(
            board = newBoard,
            player2 = restoredPlayer,
            currentPlayer = previousPlayer
          )

  def moveStone(from: Position, to: Position): Option[GameState] =
    val mayUseAnyFreePosition =
      currentPlayerObj.canFly

    val targetIsNeighbour =
      board.neighbours(from).contains(to)

    val moveIsAllowed =
      mayUseAnyFreePosition || targetIsNeighbour

    // Besitz des Startsteins und ein freies Zielfeld prüft Board.moveStone.
    // Hier bleibt nur die Spielregel für Nachbarzug bzw. Fliegen.
    if !moveIsAllowed then None
    else
      board.moveStone(from, to, currentPlayer).map: newBoard =>
        // Bei einer neu gebildeten Mühle bleibt der Spieler am Zug.
        val nextTurn =
          if MillRules.formsMillAt(newBoard, to, currentPlayer) then
            currentPlayer
          else
            nextPlayer

        copy(
          board = newBoard,
          currentPlayer = nextTurn
        )

  def undoMoveStone(from: Position, to: Position): Option[GameState] =
    // Der Stein auf dem Zielfeld gehört immer dem Spieler,
    // der den Zug rückgängig machen möchte.
    board.placedStones.get(to).flatMap: playerWhoMoved =>
      board.moveStone(to, from, playerWhoMoved).map: newBoard =>
        copy(
          board = newBoard,
          currentPlayer = playerWhoMoved
        )
