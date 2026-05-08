/*@main def millBC(): Unit =
  for n <- 1 to 3 do
    val board = MillBoard(n)
    println(s"Board Size")
    println(board.render)
    println()
    */
    import scala.io.StdIn.readLine

@main def millGame(): Unit =

  var board = MillBoard()

  val player1 = Player(PlayerId.One)
  val player2 = Player(PlayerId.Two)

  println("Welcome to Nine Men's Morris!")
  println()
  println(board.renderWithCoords)
  println()

  println(s"Player 1 stones in hand: ${player1.stonesInHand}")
  println(s"Player 2 stones in hand: ${player2.stonesInHand}")
  println()

  print("Player 1 enter position as: ring slot, example 0 3: ")
  val input = readLine()

  val parts = input.split(" ")

  if parts.length == 2 then
    val ring = parts(0).toInt
    val slot = parts(1).toInt

    val pos = Position(ring, slot)

    board.placeStone(pos, PlayerId.One) match
      case Some(newBoard) =>
        board = newBoard
        println()
        println("Stone placed!")
        println()
        println(board.render)

      case None =>
        println()
        println("This position is already occupied.")
  else
    println("Invalid input. Please enter two numbers, for example: 0 3")