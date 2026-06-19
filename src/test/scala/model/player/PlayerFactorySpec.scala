package model.player

import model.player.PlayerComponent
import model.player.PlayerId
import org.scalatest.matchers.should.Matchers
import org.scalatest.wordspec.AnyWordSpec

class PlayerFactorySpec extends AnyWordSpec with Matchers:

  "PlayerComponent" should {

    "create player one" in {
      val player = PlayerComponent.create(PlayerId.One)

      player.id should be(PlayerId.One)
      player.stonesInHand should be(9)
      player.stonesOnBoard should be(0)
    }

    "create player two" in {
      val player = PlayerComponent.create(PlayerId.Two)

      player.id should be(PlayerId.Two)
      player.stonesInHand should be(9)
      player.stonesOnBoard should be(0)
    }
  }
