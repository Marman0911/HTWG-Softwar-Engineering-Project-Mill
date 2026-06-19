package controller

import model.player.PlayerId

object GameMessages:

  def welcomeMessage: String =
    "Welcome to Nine Men's Morris!"

  def promptFor(player: PlayerId): String =
    s"Player ${if player == PlayerId.One then "1" else "2"} enter position (e.g. a1): "

  def invalidPosition: String =
    "Invalid position."

  def occupiedPosition: String =
    "Position occupied."
