@main def millBC(): Unit =
  for n <- 1 to 3 do
    val board = MillBoard(n)
    println(s"Board Size")
    println(board.render)
    println()