package controller

import model.*
import view.*
import scala.io.StdIn.readLine

object GameController:
  private def reverseCoords(board: MillBoard): Map[(Int, Int), Position] =
    board.allPositions.map(pos => board.posCoords(pos) -> pos).toMap

  private def parseInput(input: String, board: MillBoard): Option[Position] =
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

  def start(): Unit =
    val view = BoardView()
    var state = GameState()
    state.addObserver(view)

    println("Welcome to Nine Men's Morris!")
    println(view.renderWithCoords(state.board))

    while !state.player1.hasLost && !state.player2.hasLost do
      print(s"Player ${if state.currentPlayer == PlayerId.One then "1" else "2"} enter position (e.g. a1): ")
      val input = readLine()
      parseInput(input, state.board) match
        case None =>
          println("Invalid position.")
        case Some(pos) =>
          state.placeStone(pos) match
            case None    => println("Position occupied.")
            case Some(s) =>
              state = s
              // New immutable state instances do not carry observer registrations.
              state.addObserver(view)
              println(view.renderWithCoords(state.board))
              println(s"Next: Player ${if state.currentPlayer == PlayerId.One then "1" else "2"}")