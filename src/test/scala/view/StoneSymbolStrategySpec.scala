package view

import org.scalatest.matchers.should.Matchers
import org.scalatest.wordspec.AnyWordSpec

class StoneSymbolStrategySpec extends AnyWordSpec with Matchers:

  "NumberStoneSymbols" should {

    "render player ids as numbers" in {
      NumberStoneSymbols.symbol(1) should be('1')
      NumberStoneSymbols.symbol(2) should be('2')
    }
  }

  "LetterStoneSymbols" should {

    "render player ids as letters" in {
      LetterStoneSymbols.symbol(1) should be('X')
      LetterStoneSymbols.symbol(2) should be('O')
    }
  }