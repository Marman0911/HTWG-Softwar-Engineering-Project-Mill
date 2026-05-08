class BoardView:
  private val eol = sys.props("line.separator")

  def render(board: MillBoard): String =
    val rowsArr = board.rows.map(_.toCharArray)
    board.stones.foreach:
      case (pos, Some(player)) =>
        val (row, col) = board.posCoords(pos)
        rowsArr(row)(col) = if player == PlayerId.One then '1' else '2'
      case _ => ()
    rowsArr.map(_.mkString).mkString(eol)

  def renderWithCoords(board: MillBoard): String =
    val labeled = board.rows.zipWithIndex.map: (row, i) =>
      val label = if i % 2 == 0 then s"${i / 2 + 1} " else "  "
      label + row
    val footer = "  " + (0 to 2 * board.boardSize)
      .map(i => ('a' + i).toChar).mkString("    ")
    (labeled :+ footer).mkString(eol)