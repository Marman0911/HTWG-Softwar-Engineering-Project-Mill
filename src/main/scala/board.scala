/*
Every Square has 8 connection points
The print has to be from top to bottom

+--------------+--------------+
|              |              |
|    +---------+---------+    |
|    |         |         |    |
|    |    +----+----+    |    |
|    |    |         |    |    |
+----+----+         +----+----+
|    |    |         |    |    |
|    |    +----+----+    |    |
|    |         |         |    |
|    +---------+---------+    |
|              |              |
+--------------+--------------+
*/

case class Position(ring: Int, slot: Int)

object MillBoard:
  private def emptyStones(boardSize: Int): Map[Position, Option[PlayerId]] =
    (for
      r <- 0 until boardSize
      s <- 0 until 8
    yield Position(r, s) -> None).toMap

  def apply(boardSize: Int = 3): MillBoard =
    new MillBoard(boardSize, emptyStones(boardSize))

case class MillBoard private (
    boardSize: Int,
    stones: Map[Position, Option[PlayerId]]
):
  private val eol          = sys.props("line.separator")
  private val intersection = "+"
  private val step         = "-"
  private val hPath        = step * 4
  private val vPath        = "|"
  private val nml          = " " * 4

  private def posCoords(pos: Position): (Int, Int) =
    val n = boardSize
    val r = pos.ring
    pos.slot match
      case 0 => (r * 2, 5 * r)
      case 1 => (r * 2, 5 * n)
      case 2 => (r * 2, 10 * n - 5 * r)
      case 3 => (2 * n, 10 * n - 5 * r)
      case 4 => (4 * n - r * 2, 10 * n - 5 * r)
      case 5 => (4 * n - r * 2, 5 * n)
      case 6 => (4 * n - r * 2, 5 * r)
      case 7 => (2 * n, 5 * r)

  private def crossroad(n: Int): String =
    intersection + hPath * n + step * (n - 1)

  private def horizRow(k: Int): String =
    (vPath + nml) * k +
      crossroad(boardSize - k) * 2 + intersection +
      (nml + vPath) * k

  private def vertRowOuter(k: Int): String =
    val inner = " " * (5 * (boardSize - k) - 1)
    (vPath + nml) * k + vPath + inner + vPath + inner + vPath + (nml + vPath) * k

  private def vertRowInner: String =
    val gap = " " * 9
    (vPath + nml) * (boardSize - 1) + vPath + gap + vPath + (nml + vPath) * (boardSize - 1)

  private def middleRow: String =
    intersection + (hPath + intersection) * (boardSize - 1) +
      " " * 9 +
      (intersection + hPath) * (boardSize - 1) + intersection

  def rows: Seq[String] =
    val topHalf: Seq[String] =
      (0 until boardSize).flatMap: k =>
        val h = horizRow(k)
        val v = if k < boardSize - 1 then vertRowOuter(k) else vertRowInner
        Seq(h, v)

    topHalf ++ Seq(middleRow) ++ topHalf.reverse

  val allPositions: Seq[Position] =
    for
      r <- 0 until boardSize
      s <- 0 until 8
    yield Position(r, s)

  def placeStone(pos: Position, player: PlayerId): Option[MillBoard] =
    if stones.getOrElse(pos, None).isDefined then None
    else Some(copy(stones = stones.updated(pos, Some(player))))

  def render: String =
    val rowsArr = rows.map(_.toCharArray)
    stones.foreach:
      case (pos, Some(player)) =>
        val (row, col) = posCoords(pos)
        rowsArr(row)(col) = if player == PlayerId.One then '1' else '2'
      case _ => ()
    rowsArr.map(_.mkString).mkString(eol)

  def renderWithCoords: String =
    val labeled = rows.zipWithIndex.map: (row, i) =>
      val label = if i % 2 == 0 then s"${i / 2 + 1} " else "  "
      label + row

    val footer = "  " + (0 to 2 * boardSize).map(i => ('a' + i).toChar).mkString("    ")
    (labeled :+ footer).mkString(eol)
