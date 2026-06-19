package model.game

import model.board.Board
import model.board.BoardComponent
import model.player.Player
import model.player.PlayerComponent
import model.player.PlayerId

object GameComponent:

  def create(variant: GameVariant): GameState =
    impl.GameStateImpl(
      BoardComponent.create(variant.boardSize),
      PlayerComponent.create(PlayerId.One, variant.stonesPerPlayer),
      PlayerComponent.create(PlayerId.Two, variant.stonesPerPlayer)
    )

  def create(
      board: Board,
      player1: Player,
      player2: Player,
      currentPlayer: PlayerId
  ): GameState =
    impl.GameStateImpl(board, player1, player2, currentPlayer)

  def standard: GameState =
    create(StandardMill)

  def small: GameState =
    create(SmallMill)