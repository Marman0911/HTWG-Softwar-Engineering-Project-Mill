package view

import model.PlayerId
import org.scalatest.matchers.should.Matchers
import org.scalatest.wordspec.AnyWordSpec

class GameMessagesSpec extends AnyWordSpec with Matchers:

  "GameMessages" should {

    "provide the welcome message" in {
      GameMessages.welcomeMessage should be("Welcome to Nine Men's Morris!")
    }

    "provide prompts for both players" in {
      GameMessages.promptFor(PlayerId.One) should be("Player 1 enter position (e.g. a1): ")
      GameMessages.promptFor(PlayerId.Two) should be("Player 2 enter position (e.g. a1): ")
    }

    "provide error messages" in {
      GameMessages.invalidPosition should be("Invalid position.")
      GameMessages.occupiedPosition should be("Position occupied.")
    }
  }
