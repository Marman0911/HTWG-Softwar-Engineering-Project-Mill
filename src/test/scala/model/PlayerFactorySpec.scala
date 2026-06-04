package model

import org.scalatest.matchers.should.Matchers
import org.scalatest.wordspec.AnyWordSpec

class PlayerFactorySpec extends AnyWordSpec with Matchers:

  "PlayerFactory" should {

    "create player one" in {
      val player = PlayerFactory.create(PlayerId.One)

      player.id should be(PlayerId.One)
      player.stonesInHand should be(9)
      player.stonesOnBoard should be(0)
    }

    "create player two" in {
      val player = PlayerFactory.create(PlayerId.Two)

      player.id should be(PlayerId.Two)
      player.stonesInHand should be(9)
      player.stonesOnBoard should be(0)
    }
  }