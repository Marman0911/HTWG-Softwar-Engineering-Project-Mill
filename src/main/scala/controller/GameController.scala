package controller

import model.*
import view.*
import scala.io.StdIn.readLine

object GameController:
  private[controller] def reverseCoords(board: MillBoard): Map[(Int, Int), Position] =
    board.allPositions.map(pos => board.posCoords(pos) -> pos).toMap

  private[controller] def parseInput(input: String, board: MillBoard): Option[Position] =
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

  private[controller] def handleTurnInput(state: GameState, input: String, view: BoardView): (GameState, Seq[String]) =
    parseInput(input, state.board) match
      case None =>
        (state, Seq("Invalid position."))
      case Some(pos) =>
        state.placeStone(pos) match
          case None =>
            (state, Seq("Position occupied."))
          case Some(nextState) =>
            // New immutable state instances do not carry observer registrations.
            nextState.addObserver(view)
            (
              nextState,
              Seq(
                view.renderWithCoords(nextState.board),
                s"Next: Player ${if nextState.currentPlayer == PlayerId.One then "1" else "2"}"
              )
            )

  def start(): Unit =
    val view = BoardView()
    var state = GameState()
    state.addObserver(view)

    println("Welcome to Nine Men's Morris!")
    println(view.renderWithCoords(state.board))

    while !state.player1.hasLost && !state.player2.hasLost do
      print(s"Player ${if state.currentPlayer == PlayerId.One then "1" else "2"} enter position (e.g. a1): ")
      val input = readLine()
      val (nextState, outputLines) = handleTurnInput(state, input, view)
      state = nextState
      outputLines.foreach(println)