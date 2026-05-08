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

  println(BoardView().renderWithCoords(board))
  println()

  println(s"Player 1 stones in hand: ${player1.stonesInHand}")
  println(s"Player 2 stones in hand: ${player2.stonesInHand}")
  println()

  val view = BoardView()

  print("Player 1 enter position (e.g. a1, 1a, A1): ")
  val input = readLine()

  view.parseInput(input, board) match
    case None =>
      println("Invalid position. Use letter (a-g) and number (1-7), e.g. a1 or 1a.")
    case Some(pos) =>
      board.placeStone(pos, PlayerId.One) match
        case Some(newBoard) =>
          board = newBoard
          println()
          println("Stone placed!")
          println()
          println(view.renderWithCoords(board))
        case None =>
          println()
          println("This position is already occupied.")