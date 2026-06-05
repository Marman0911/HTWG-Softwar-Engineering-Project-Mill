package controller

import model.GameState
import model.MillBoard
import model.PlayerId
import model.Position
import view.BoardViewMapper
import view.BoardViewModel
import view.GameMessages

trait GameObserver:
  def update(viewModel: BoardViewModel): Unit

object GameController:

  private var observers: List[GameObserver] = List.empty

  def addObserver(observer: GameObserver): Unit =
    observers = observer :: observers

  def removeObserver(observer: GameObserver): Unit =
    observers = observers.filterNot(_ eq observer)

  private[controller] def clearObservers(): Unit =
    observers = List.empty

  private def notifyObservers(state: GameState): Unit =
    val viewModel = BoardViewMapper.toViewModel(state)
    observers.foreach(_.update(viewModel))

  def runGameLoop(
    state: GameState,
    readInput: () => String,
    promptOut: String => Unit,
    lineOut: String => Unit
  ): GameState =
    if shouldContinue(state) then
      promptOut(promptFor(state.currentPlayer))
      val input = readInput()

      handleTurnInput(state, input) match
        case Left(message) =>
          lineOut(message)
          runGameLoop(state, readInput, promptOut, lineOut)
        case Right(nextState) =>
          notifyObservers(nextState)
          runGameLoop(nextState, readInput, promptOut, lineOut)
    else
      state

  private[controller] def shouldContinue(state: GameState): Boolean =
    !state.player1.hasLost && !state.player2.hasLost

  private[controller] def promptFor(player: PlayerId): String =
    GameMessages.promptFor(player)

  def welcomeMessage: String =
    GameMessages.welcomeMessage

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
          reverseCoords(board).get((rowNum - 1) * 2, colIdx * 5)

  private[controller] def handleTurnInput(state: GameState, input: String): Either[String, GameState] =
    parseInput(input, state.board) match
      case None =>
        Left(GameMessages.invalidPosition)

      case Some(pos) =>
        state.placeStone(pos) match
          case None =>
            Left(GameMessages.occupiedPosition)

          case Some(nextState) =>
            Right(nextState)