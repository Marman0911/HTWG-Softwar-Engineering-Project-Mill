package model.game

import model.game.SmallMill
import model.game.StandardMill
import org.scalatest.flatspec.AnyFlatSpec
import org.scalatest.matchers.should.Matchers

class GameVariantSpec extends AnyFlatSpec with Matchers:

  "StandardMill" should "have the correct configuration for Nine Men's Morris" in:
    StandardMill.boardSize shouldBe 3
    StandardMill.stonesPerPlayer shouldBe 9
    StandardMill.name shouldBe "Nine Men's Morris"

  "SmallMill" should "have the correct configuration for Six Men's Morris" in:
    SmallMill.boardSize shouldBe 2
    SmallMill.stonesPerPlayer shouldBe 6
    SmallMill.name shouldBe "Six Men's Morris"
