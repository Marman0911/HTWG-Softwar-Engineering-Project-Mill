package controller

import model.GameState
import model.MillBoard
import model.Position
import scala.util.Try

trait GameObserver:
  def update(): Unit

trait Observable:
  private var _observers: List[GameObserver] = List.empty

  def addObserver(observer: GameObserver): Unit =
    _observers = observer :: _observers

  def removeObserver(observer: GameObserver): Unit =
    _observers = _observers.filterNot(_ eq observer)

  protected def notifyObservers(): Unit =
    _observers.foreach(_.update())

class GameController(initialState: GameState = GameState()) extends Observable:

  private var state: GameState = initialState
  private var history: List[GameState] = Nil
  private var phase: GamePhase = PlacingPhase(parseInput)

  def isGameOver: Boolean = !shouldContinue(state)

  def boardViewModel: BoardViewModel = BoardViewMapper.toViewModel(state)

  def currentPrompt: String = phase.prompt
  
  def handleInput(input: String): Either[String, Unit] =
    input.trim.toLowerCase match
      case "undo" => undo()
      case _ =>
        phase.handleInput(input, state) match
          case Left(message)    => Left(message)
          case Right(nextState) =>
            history = state :: history
            state = nextState
            phase = phase.next(state)
            notifyObservers()
            Right(())

  def welcomeMessage: String =
    GameMessages.welcomeMessage

  def undo(): Either[String, Unit] =
  history match
    case Nil          => Left("Nothing to undo.")
    case prev :: rest =>
      state = prev
      history = rest
      notifyObservers()
      Right(())

  private[controller] def shouldContinue(state: GameState): Boolean =
    !state.player1.hasLost && !state.player2.hasLost

  private[controller] def reverseCoords(board: MillBoard): Map[(Int, Int), Position] =
    board.allPositions.map(pos => board.posCoords(pos) -> pos).toMap

  private[controller] def parseInput(input: String, board: MillBoard): Option[Position] =
    val clean = input.trim.toLowerCase.filter(c => c.isLetter || c.isDigit)
    for
      _      <- Option.when(clean.length >= 2)(())
      letter  = if clean.head.isLetter then clean.head else clean.last
      number  = if clean.head.isLetter then clean.tail else clean.init
      colIdx  = letter - 'a'
      rowNum <- Try(number.toInt).toOption   // Try statt toIntOption – kein try-catch
      pos    <- reverseCoords(board).get((rowNum - 1) * 2, colIdx * 5)
    yield pos
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