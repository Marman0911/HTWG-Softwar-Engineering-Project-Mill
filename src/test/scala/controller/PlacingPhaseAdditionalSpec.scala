package controller

import model.board.BoardComponent
import model.game.GameComponent
import model.player.{PlayerComponent, PlayerId}
import org.scalatest.matchers.should.Matchers
import org.scalatest.wordspec.AnyWordSpec

class PlacingPhaseAdditionalSpec extends AnyWordSpec with Matchers:

  "PlacingPhase.next" should {

    "stay in PlacingPhase when only one player has placed all stones" in {
      val state =
        GameComponent.create(
          BoardComponent.create(3),
          PlayerComponent.create(PlayerId.One, stonesInHand = 0),
          PlayerComponent.create(PlayerId.Two, stonesInHand = 1),
          PlayerId.One
        )

      PlacingPhase((_, _) => None).next(state) shouldBe a[PlacingPhase]
    }
  }
