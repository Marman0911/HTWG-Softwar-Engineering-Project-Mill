import org.scalatest.matchers.should.Matchers
import org.scalatest.wordspec.AnyWordSpec

class MillBoardSpec extends AnyWordSpec with Matchers: 
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
          "+----+----+",
        ),
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
      val rows  = board.rows

      rows(1).count(_ == '|') should be(3) // outer vertical row
      rows(3).count(_ == '|') should be(4) // inner vertical row mirrored in lower half
      rows(4) should include("         ") // middle gap
    }

    "render by joining rows with the platform line separator" in {
      val board = MillBoard(3)
      val eol   = sys.props("line.separator")
      board.render should be(board.rows.mkString(eol))
    }
  }
