import org.scalatest.wordspec.AnyWordSpec
import org.scalatest.matchers.should.Matchers

class PlayerSpec extends AnyWordSpec with Matchers{

  "A Player" when {
    "created with default values" should {
      "have PlayerId.One" in {
        val player = Player(PlayerId.One)
        player.id should be(PlayerId.One)
      }

      "have 9 stones in hand" in {
        val player = Player(PlayerId.One)
        player.stonesInHand should be(9)
      }

      "have 0 stones on board" in {
        val player = Player(PlayerId.One)
        player.stonesOnBoard should be(0)
      }

      "have total of 9 stones" in {
        val player = Player(PlayerId.One)
        player.totalStones should be(9)
      }

      "not have lost" in {
        val player = Player(PlayerId.One)
        player.hasLost should be(false)
      }

      "not be able to fly" in {
        val player = Player(PlayerId.One)
        player.canFly should be(false)
      }
    }

    "created with PlayerId.Two" should {
      "have correct id" in {
        val player = Player(PlayerId.Two)
        player.id should be(PlayerId.Two)
      }
    }

    "created with custom stone values" should {
      "have correct stones in hand" in {
        val player = Player(PlayerId.One, stonesInHand = 5)
        player.stonesInHand should be(5)
      }

      "have correct stones on board" in {
        val player = Player(PlayerId.One, stonesOnBoard = 3)
        player.stonesOnBoard should be(3)
      }

      "calculate total stones correctly" in {
        val player = Player(PlayerId.One, stonesInHand = 4, stonesOnBoard = 3)
        player.totalStones should be(7)
      }

      "have correct total with various combinations" in {
        val player1 = Player(PlayerId.One, stonesInHand = 6, stonesOnBoard = 6)
        player1.totalStones should be(12)

        val player2 = Player(PlayerId.One, stonesInHand = 0, stonesOnBoard = 9)
        player2.totalStones should be(9)

        val player3 = Player(PlayerId.One, stonesInHand = 9, stonesOnBoard = 0)
        player3.totalStones should be(9)
      }
    }

    "checking game state with hasLost" should {
      "return false when total stones = 3" in {
        val player = Player(PlayerId.One, stonesInHand = 2, stonesOnBoard = 1)
        player.hasLost should be(false)
      }

      "return true when total stones = 2" in {
        val player = Player(PlayerId.One, stonesInHand = 2, stonesOnBoard = 0)
        player.hasLost should be(true)
      }

      "return true when total stones = 1" in {
        val player = Player(PlayerId.One, stonesInHand = 1, stonesOnBoard = 0)
        player.hasLost should be(true)
      }

      "return true when total stones = 0" in {
        val player = Player(PlayerId.One, stonesInHand = 0, stonesOnBoard = 0)
        player.hasLost should be(true)
      }

      "return false when total stones > 3" in {
        val player = Player(PlayerId.One, stonesInHand = 5, stonesOnBoard = 2)
        player.hasLost should be(false)
      }
    }

    "checking flying ability with canFly" should {
      "return true when exactly 3 stones on board and 0 in hand" in {
        val player = Player(PlayerId.One, stonesInHand = 0, stonesOnBoard = 3)
        player.canFly should be(true)
      }

      "return false when 3 stones on board but stones in hand" in {
        val player = Player(PlayerId.One, stonesInHand = 1, stonesOnBoard = 3)
        player.canFly should be(false)
      }

      "return false when 0 stones in hand but not 3 on board" in {
        val player = Player(PlayerId.One, stonesInHand = 0, stonesOnBoard = 2)
        player.canFly should be(false)
      }

      "return false when stones on board and stones in hand" in {
        val player = Player(PlayerId.One, stonesInHand = 3, stonesOnBoard = 3)
        player.canFly should be(false)
      }

      "return false for other edge cases" in {
        Player(PlayerId.One, stonesInHand = 9, stonesOnBoard = 0).canFly should be(false)
        Player(PlayerId.One, stonesInHand = 0, stonesOnBoard = 9).canFly should be(false)
        Player(PlayerId.One, stonesInHand = 4, stonesOnBoard = 2).canFly should be(false)
      }
    }

    "combining multiple conditions" should {
      "have player that lost and cannot fly" in {
        val player = Player(PlayerId.One, stonesInHand = 2, stonesOnBoard = 0)
        player.hasLost should be(true)
        player.canFly should be(false)
      }

      "have player that didn't lose but can fly" in {
        val player = Player(PlayerId.One, stonesInHand = 0, stonesOnBoard = 3)
        player.hasLost should be(false)
        player.canFly should be(true)
      }

      "have normal active player" in {
        val player = Player(PlayerId.One, stonesInHand = 5, stonesOnBoard = 2)
        player.hasLost should be(false)
        player.canFly should be(false)
        player.totalStones should be(7)
      }
    }
  }

  "PlayerId enum" should {
    "have One value" in {
      PlayerId.One should not be null
    }

    "have Two value" in {
      PlayerId.Two should not be null
    }

    "have different values" in {
      PlayerId.One should not be PlayerId.Two
    }
  }
}