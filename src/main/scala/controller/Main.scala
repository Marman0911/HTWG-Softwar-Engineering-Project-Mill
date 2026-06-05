package controller

import model.GameState
import scala.io.StdIn.readLine
import view.BoardView

private[controller] def runGameLoop(
  state: GameState,
  readInput: () => String,
  promptOut: String => Unit,
  lineOut: String => Unit
): GameState =
  if GameController.shouldContinue(state) then
    promptOut(GameController.promptFor(state.currentPlayer))
    val input = readInput()

    GameController.handleTurnInput(state, input) match
      case Left(message) =>
        lineOut(message)
        runGameLoop(state, readInput, promptOut, lineOut)
      case Right(nextState) =>
        runGameLoop(nextState, readInput, promptOut, lineOut)
  else
    state

@main def millGame(): Unit =
  val view = BoardView()
  val state = GameState().addObserver(view)

  println(GameController.welcomeMessage)
  println(view.renderWithCoords(state.board))

  runGameLoop(state, () => readLine(), print, println)