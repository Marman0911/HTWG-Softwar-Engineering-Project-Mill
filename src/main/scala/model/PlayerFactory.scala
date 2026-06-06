package model

object PlayerFactory:

  def create(id: PlayerId): Player =
    Player(id)