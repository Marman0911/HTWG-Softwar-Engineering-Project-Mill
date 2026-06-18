package view

trait StoneSymbolStrategy:
  def symbol(playerNumber: Int): Char

object NumberStoneSymbols extends StoneSymbolStrategy:
  def symbol(playerNumber: Int): Char =
    if playerNumber == 1 then '1' else '2'

object LetterStoneSymbols extends StoneSymbolStrategy:
  def symbol(playerNumber: Int): Char =
    if playerNumber == 1 then 'X' else 'O'

object EmojiStoneSymbols extends StoneSymbolStrategy:
  def symbol(playerNumber: Int): Char =
    if playerNumber == 1 then '●' else '○'
