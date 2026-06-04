package model

// Factory Method, Player wird jetzt über zentrale Factory erzeugt

object PlayerFactory:

  def create(id: PlayerId): Player =
    Player(id)