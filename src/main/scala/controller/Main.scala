package controller

import model.GameState
import scala.io.StdIn.readLine
import view.BoardView

@main def millGame(): Unit =
  val view = BoardView()
  var state = GameState().addObserver(view)

  println(GameController.welcomeMessage)
  println(view.renderWithCoords(state.board))

  while GameController.shouldContinue(state) do
    print(GameController.promptFor(state.currentPlayer))
    val input = readLine()

    GameController.handleTurnInput(state, input) match
      case Left(message) =>
        println(message)
      case Right(nextState) =>
        state = nextState