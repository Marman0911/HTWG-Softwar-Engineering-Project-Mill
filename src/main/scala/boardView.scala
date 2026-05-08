class BoardView:
  private val eol = sys.props("line.separator")

  private def stoneRows(board: MillBoard): Seq[String] =
    val rowsArr = board.rows.map(_.toCharArray)
    board.stones.foreach:
      case (pos, Some(player)) =>
        val (row, col) = board.posCoords(pos)
        rowsArr(row)(col) = if player == PlayerId.One then '1' else '2'
      case _ => ()
    rowsArr.map(_.mkString)

  def render(board: MillBoard): String =
    stoneRows(board).mkString(eol)

  def renderWithCoords(board: MillBoard): String =
    val labeled = stoneRows(board).zipWithIndex.map: (row, i) =>
      val label = if i % 2 == 0 then s"${i / 2 + 1} " else "  "
      label + row
    val footer = "  " + (0 to 2 * board.boardSize)
      .map(i => ('a' + i).toChar).mkString("    ")
    (labeled :+ footer).mkString(eol)

  private def reverseCoords(board: MillBoard): Map[(Int, Int), Position] =
    board.allPositions.map(pos => board.posCoords(pos) -> pos).toMap

  def parseInput(input: String, board: MillBoard): Option[Position] =
    val clean = input.trim.toLowerCase.filter(c => c.isLetter || c.isDigit)
    if clean.length < 2 then None
    else
      val (letter, number) =
        if clean.head.isLetter then (clean.head, clean.tail)
        else (clean.last, clean.init)
      val colIdx = letter - 'a'
      number.toIntOption match
        case None => None
        case Some(rowNum) =>
          if colIdx < 0 || colIdx > 2 * board.boardSize
            || rowNum < 1 || rowNum > 2 * board.boardSize + 1
          then None
          else reverseCoords(board).get((rowNum - 1) * 2, colIdx * 5)