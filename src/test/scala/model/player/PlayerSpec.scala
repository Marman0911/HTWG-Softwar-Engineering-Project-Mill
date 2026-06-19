package model.player

import org.scalatest.flatspec.AnyFlatSpec
import org.scalatest.matchers.should.Matchers

class PlayerSpec extends AnyFlatSpec with Matchers:

  "Player" should "use default values" in:
    val player = PlayerComponent.create(PlayerId.One)

    player.id shouldBe PlayerId.One
    player.stonesInHand shouldBe 9
    player.stonesOnBoard shouldBe 0

  it should "create player two correctly" in:
    val player = PlayerComponent.create(PlayerId.Two)

    player.id shouldBe PlayerId.Two
    player.stonesInHand shouldBe 9
    player.stonesOnBoard shouldBe 0

  it should "calculate total stones" in:
    val player = PlayerComponent.create(
      PlayerId.One,
      stonesInHand = 4,
      stonesOnBoard = 3
    )

    player.totalStones shouldBe 7

  it should "report lost state when total stones are below three" in:
    val player = PlayerComponent.create(
      PlayerId.One,
      stonesInHand = 1,
      stonesOnBoard = 1
    )

    player.hasLost shouldBe true

  it should "not report lost state when total stones are three" in:
    val player = PlayerComponent.create(
      PlayerId.One,
      stonesInHand = 0,
      stonesOnBoard = 3
    )

    player.hasLost shouldBe false

  it should "not report lost state when total stones are above three" in:
    val player = PlayerComponent.create(
      PlayerId.One,
      stonesInHand = 4,
      stonesOnBoard = 3
    )

    player.hasLost shouldBe false

  it should "allow flying only with exactly three stones on board and none in hand" in:
    val player = PlayerComponent.create(
      PlayerId.One,
      stonesInHand = 0,
      stonesOnBoard = 3
    )

    player.canFly shouldBe true

  it should "not allow flying when player has less than three stones on board" in:
    val player = PlayerComponent.create(
      PlayerId.One,
      stonesInHand = 0,
      stonesOnBoard = 2
    )

    player.canFly shouldBe false

  it should "not allow flying when player has more than three stones on board" in:
    val player = PlayerComponent.create(
      PlayerId.One,
      stonesInHand = 0,
      stonesOnBoard = 4
    )

    player.canFly shouldBe false

  it should "not allow flying while player still has stones in hand" in:
    val player = PlayerComponent.create(
      PlayerId.One,
      stonesInHand = 1,
      stonesOnBoard = 3
    )

    player.canFly shouldBe false

  it should "not allow flying when both conditions are false" in:
    val player = PlayerComponent.create(
      PlayerId.One,
      stonesInHand = 2,
      stonesOnBoard = 4
    )

    player.canFly shouldBe false