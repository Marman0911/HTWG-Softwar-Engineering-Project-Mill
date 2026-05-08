/*@main def millBC(): Unit =
  for n <- 1 to 3 do
    val board = MillBoard(n)
    println(s"Board Size")
    println(board.render)
    println()
    */
    import scala.io.StdIn.readLine

@main def millGame(): Unit =

  val board = MillBoard()

  val player1 = Player(PlayerId.One)
  val player2 = Player(PlayerId.Two)

  println("Welcome to Nine Men's Morris!")
  println()

  println(board.renderWithCoords)
  println()

  println(s"Player 1 stones in hand: ${player1.stonesInHand}")
  println(s"Player 2 stones in hand: ${player2.stonesInHand}")
  println()

  print("Player 1 enter a move: ")

  val input = readLine()

  println()
  println(s"You entered: $input")