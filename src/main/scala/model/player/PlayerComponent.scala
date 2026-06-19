package model.player

object PlayerComponent:

  def create(
      id: PlayerId,
      stonesInHand: Int = 9,
      stonesOnBoard: Int = 0
  ): Player =
    impl.PlayerImpl(
      id = id,
      stonesInHand = stonesInHand,
      stonesOnBoard = stonesOnBoard
    )