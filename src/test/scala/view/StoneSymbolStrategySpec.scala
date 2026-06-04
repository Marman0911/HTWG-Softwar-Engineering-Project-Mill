package view

import model.PlayerId
import org.scalatest.matchers.should.Matchers
import org.scalatest.wordspec.AnyWordSpec

class StoneSymbolStrategySpec extends AnyWordSpec with Matchers:

  "NumberStoneSymbols" should {

    "render player ids as numbers" in {
      NumberStoneSymbols.symbol(PlayerId.One) should be('1')
      NumberStoneSymbols.symbol(PlayerId.Two) should be('2')
    }
  }

  "LetterStoneSymbols" should {

    "render player ids as letters" in {
      LetterStoneSymbols.symbol(PlayerId.One) should be('X')
      LetterStoneSymbols.symbol(PlayerId.Two) should be('O')
    }
  }