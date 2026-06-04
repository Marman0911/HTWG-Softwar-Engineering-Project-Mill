package view

//Strategy Plattern weil Darstellungsart der Steine ausgetauscht werden können.

import model.PlayerId

trait StoneSymbolStrategy:
  def symbol(player: PlayerId): Char

object NumberStoneSymbols extends StoneSymbolStrategy:
  def symbol(player: PlayerId): Char =
    if player == PlayerId.One then '1' else '2'

object LetterStoneSymbols extends StoneSymbolStrategy:
  def symbol(player: PlayerId): Char =
    if player == PlayerId.One then 'X' else 'O'