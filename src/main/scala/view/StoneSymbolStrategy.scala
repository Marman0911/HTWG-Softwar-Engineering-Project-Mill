package view

//Strategy Plattern weil Darstellungsart der Steine ausgetauscht werden können.

trait StoneSymbolStrategy:
  def symbol(playerNumber: Int): Char

object NumberStoneSymbols extends StoneSymbolStrategy:
  def symbol(playerNumber: Int): Char =
    if playerNumber == 1 then '1' else '2'

object LetterStoneSymbols extends StoneSymbolStrategy:
  def symbol(playerNumber: Int): Char =
    if playerNumber == 1 then 'X' else 'O'