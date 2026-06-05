package controller

import model.GameState
import model.PlayerId
import scala.io.StdIn.readLine
import view.BoardView
import view.BoardViewModel
import view.StonePlacement

private[controller] def toViewModel(state: GameState): BoardViewModel =
  val stones = state.board.stones.collect:
    case (pos, Some(player)) =>
      val (row, col) = state.board.posCoords(pos)
      val playerNumber = if player == PlayerId.One then 1 else 2
      StonePlacement(row, col, playerNumber)

  BoardViewModel(
    rows = state.board.rows,
    boardSize = state.board.boardSize,
    stones = stones.toSeq,
    nextPlayerNumber = if state.currentPlayer == PlayerId.One then 1 else 2
  )

private[controller] def runGameLoop(
  state: GameState,
  readInput: () => String,
  promptOut: String => Unit,
  lineOut: String => Unit,
  renderState: GameState => Unit
): GameState =
  if GameController.shouldContinue(state) then
    promptOut(GameController.promptFor(state.currentPlayer))
    val input = readInput()

    GameController.handleTurnInput(state, input) match
      case Left(message) =>
        lineOut(message)
        runGameLoop(state, readInput, promptOut, lineOut, renderState)
      case Right(nextState) =>
        renderState(nextState)
        runGameLoop(nextState, readInput, promptOut, lineOut, renderState)
  else
    state

@main def millGame(): Unit =
  val view = BoardView()
  val state = GameState()

  println(GameController.welcomeMessage)
  println(view.renderWithCoords(toViewModel(state)))

  runGameLoop(state, () => readLine(), print, println, state => view.update(toViewModel(state)))