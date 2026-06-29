package model.game

import model.board.{BoardComponent, Position}
import org.scalatest.matchers.should.Matchers
import org.scalatest.wordspec.AnyWordSpec

class MillRulesAdditionalSpec extends AnyWordSpec with Matchers:

  "MillRules.allMills" should {

    "include every adjacent three-ring line on a four-ring board" in {
      val mills =
        MillRules.allMills(BoardComponent.create(4))

      mills.size shouldBe 24
      mills should contain(
        Seq(Position(1, 1), Position(2, 1), Position(3, 1))
      )
    }
  }
