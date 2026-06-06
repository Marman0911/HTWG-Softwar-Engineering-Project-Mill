package controller

import org.scalatest.matchers.should.Matchers
import org.scalatest.wordspec.AnyWordSpec

class BoardViewMapperSpec extends AnyWordSpec with Matchers:

  "BoardViewMapper.boardRows" should {

    "produce 13 rows for the default board size 3" in {
      BoardViewMapper.boardRows(3).length should be(13)
    }

    "create the expected rows for size one" in {
      BoardViewMapper.boardRows(1) should be(
        Seq(
          "+----+----+",
          "|         |",
          "+         +",
          "|         |",
          "+----+----+"
        )
      )
    }

    "create the expected key rows for size three" in {
      val rows = BoardViewMapper.boardRows(3)

      rows(0) should be("+--------------+--------------+")
      rows(1) should be("|              |              |")
      rows(2) should be("|    +---------+---------+    |")
      rows(3) should be("|    |         |         |    |")
      rows(4) should be("|    |    +----+----+    |    |")
      rows(5) should be("|    |    |         |    |    |")
      rows(6) should be("+----+----+         +----+----+")
    }

    "build a mirrored shape around the middle row" in {
      val rows = BoardViewMapper.boardRows(3)
      rows should be(rows.reverse)
    }
  }
