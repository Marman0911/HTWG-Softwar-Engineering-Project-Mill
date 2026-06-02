package model

import org.scalatest.matchers.should.Matchers
import org.scalatest.wordspec.AnyWordSpec

class PlayerSpec extends AnyWordSpec with Matchers:

  "Player" should {

    "use default values" in {
      val player = Player(PlayerId.One)

      player.id should be(PlayerId.One)
      player.stonesInHand should be(9)
      player.stonesOnBoard should be(0)
      player.totalStones should be(9)
      player.hasLost should be(false)
      player.canFly should be(false)
    }

    "calculate total stones" in {
      val player = Player(PlayerId.Two, stonesInHand = 4, stonesOnBoard = 3)
      player.totalStones should be(7)
    }

    "report lost state when total stones are below three" in {
      Player(PlayerId.One, stonesInHand = 2, stonesOnBoard = 0).hasLost should be(true)
      Player(PlayerId.One, stonesInHand = 2, stonesOnBoard = 1).hasLost should be(false)
    }

    "allow flying only with exactly three stones on board and none in hand" in {
      Player(PlayerId.One, stonesInHand = 0, stonesOnBoard = 3).canFly should be(true)
      Player(PlayerId.One, stonesInHand = 1, stonesOnBoard = 3).canFly should be(false)
      Player(PlayerId.One, stonesInHand = 0, stonesOnBoard = 2).canFly should be(false)
    }
  }
