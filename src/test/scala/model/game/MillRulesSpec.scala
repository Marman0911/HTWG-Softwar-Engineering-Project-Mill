package model.game

import model.board.{Board, BoardComponent, Position}
import model.player.PlayerId
import org.scalatest.matchers.should.Matchers
import org.scalatest.wordspec.AnyWordSpec

class MillRulesSpec extends AnyWordSpec with Matchers:

  private def boardWith(
      stones: Seq[(Position, PlayerId)]
  ): Board =
    stones.foldLeft(BoardComponent.create(3)): (board, stone) =>
      board.placeStone(stone._1, stone._2).get

  "MillRules.allMills" should {

    "contain the 16 possible mills on a standard board" in {
      val mills =
        MillRules.allMills(BoardComponent.create(3))

      mills.size shouldBe 16
      mills should contain(Seq(Position(0, 0), Position(0, 1), Position(0, 2)))
      mills should contain(Seq(Position(0, 1), Position(1, 1), Position(2, 1)))
    }

    "contain only ring mills on a board with fewer than three rings" in {
      val mills =
        MillRules.allMills(BoardComponent.create(2))

      mills.size shouldBe 8
      mills should not contain Seq(Position(0, 1), Position(1, 1), Position(2, 1))
    }
  }

  "MillRules.millsOf" should {

    "recognize a complete mill on one ring" in {
      val topMill =
        Seq(Position(0, 0), Position(0, 1), Position(0, 2))

      val board =
        boardWith(topMill.map(_ -> PlayerId.One))

      MillRules.millsOf(board, PlayerId.One) should contain(topMill)
      MillRules.formsMillAt(board, Position(0, 1), PlayerId.One) shouldBe true
      MillRules.isPartOfMill(board, Position(0, 2), PlayerId.One) shouldBe true
    }

    "recognize a mill between the three rings" in {
      val verticalMill =
        Seq(Position(0, 3), Position(1, 3), Position(2, 3))

      val board =
        boardWith(verticalMill.map(_ -> PlayerId.Two))

      MillRules.millsOf(board, PlayerId.Two) should contain(verticalMill)
      MillRules.formsMillAt(board, Position(2, 3), PlayerId.Two) shouldBe true
    }

    "not recognize an incomplete mill" in {
      val board =
        boardWith(
          Seq(
            Position(0, 0) -> PlayerId.One,
            Position(0, 1) -> PlayerId.One
          )
        )

      MillRules.millsOf(board, PlayerId.One) shouldBe empty
      MillRules.isPartOfMill(board, Position(0, 0), PlayerId.One) shouldBe false
      MillRules.formsMillAt(board, Position(0, 1), PlayerId.One) shouldBe false
    }
  }

  "MillRules.removableOpponentPositions" should {

    "allow only opponent stones outside a mill when one exists" in {
      val board =
        boardWith(
          Seq(
            Position(0, 0) -> PlayerId.Two,
            Position(0, 1) -> PlayerId.Two,
            Position(0, 2) -> PlayerId.Two,
            Position(1, 0) -> PlayerId.Two
          )
        )

      MillRules.removableOpponentPositions(board, PlayerId.One) shouldBe
        Set(Position(1, 0))
    }

    "allow every opponent stone when all opponent stones are in mills" in {
      val opponentMill =
        Set(Position(0, 0), Position(0, 1), Position(0, 2))

      val board =
        boardWith(opponentMill.toSeq.map(_ -> PlayerId.Two))

      MillRules.removableOpponentPositions(board, PlayerId.One) shouldBe opponentMill
    }
  }
