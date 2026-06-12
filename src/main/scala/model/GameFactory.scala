package model

object GameFactory:
  def create(variant: GameVariant): GameState =
    GameState(
      MillBoard(variant.boardSize),
      PlayerFactory.create(PlayerId.One),
      PlayerFactory.create(PlayerId.Two)
    )

  def standard: GameState = create(StandardMill)
  def small: GameState    = create(SmallMill)