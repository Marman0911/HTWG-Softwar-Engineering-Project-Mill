import org.scalatest.matchers.should.Matchers
import org.scalatest.wordspec.AnyWordSpec

class MillBoardSpec extends AnyWordSpec with Matchers {

  "MillBoard" should {

    "use default board size when constructed without argument" in {
      val board = MillBoard()
      board.rows.length should be(13) // 4 * 3 + 1
    }

    "create the expected minimal board for size 1" in {
      val board = MillBoard(1)

      board.rows should be(
        Seq(
          "+----+----+",
          "|         |",
          "+         +",
          "|         |",
          "+----+----+"
        )
      )
    }

    "create the expected number of rows for common board sizes" in {
      (1 to 3).foreach { n =>
        val board = MillBoard(n)
        board.rows.length should be(4 * n + 1)
      }
    }

    "build rows as a mirrored shape around the middle row" in {
      val board = MillBoard(3)
      board.rows should be(board.rows.reverse)
    }

    "include outer and inner vertical patterns for size 2" in {
      val board = MillBoard(2)
      val rows = board.rows

      rows(1).count(_ == '|') should be(3)
      rows(3).count(_ == '|') should be(4)
      rows(4) should include("         ")
    }

    "place a stone for Player One at the given position" in {
      val board = MillBoard()
      val pos = Position(0, 0)

      val result = board.placeStone(pos, PlayerId.One)

      result shouldBe defined
      result.get.stones(pos) should be(Some(PlayerId.One))
    }

    "place a stone for Player Two at the given position" in {
      val board = MillBoard()
      val pos = Position(0, 2)

      val result = board.placeStone(pos, PlayerId.Two)

      result shouldBe defined
      result.get.stones(pos) should be(Some(PlayerId.Two))
    }

    "not modify the original board after placeStone" in {
      val board = MillBoard()
      val pos = Position(0, 0)

      board.placeStone(pos, PlayerId.One)

      board.stones(pos) should be(None)
    }

    "return None when placing on an occupied position" in {
      val board = MillBoard()
      val pos = Position(1, 0)

      val after = board.placeStone(pos, PlayerId.One).get

      after.placeStone(pos, PlayerId.Two) should be(None)
    }

    "accumulate stones from multiple moves" in {
      val board = MillBoard()

      val pos1 = Position(0, 0)
      val pos2 = Position(0, 2)

      val result =
        board
          .placeStone(pos1, PlayerId.One).get
          .placeStone(pos2, PlayerId.Two).get

      result.stones(pos1) should be(Some(PlayerId.One))
      result.stones(pos2) should be(Some(PlayerId.Two))
    }

    "have all positions empty on a fresh board" in {
      val board = MillBoard()

      board.stones.values.foreach(_ should be(None))
    }

    "render by joining rows with the platform line separator" in {
      val board = MillBoard(3)
      val eol = sys.props("line.separator")

      BoardView().render(board) should be(board.rows.mkString(eol))
    }
  }
}