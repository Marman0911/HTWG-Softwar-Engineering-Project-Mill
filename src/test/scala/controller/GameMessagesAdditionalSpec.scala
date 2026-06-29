package controller

import org.scalatest.matchers.should.Matchers
import org.scalatest.wordspec.AnyWordSpec

class GameMessagesAdditionalSpec extends AnyWordSpec with Matchers:

  "GameMessages.invalidMove" should {

    "explain that a move needs an own stone and a free neighbour" in {
      GameMessages.invalidMove shouldBe
        "Invalid move. Select one of your stones and a free neighbouring point."
    }
  }
