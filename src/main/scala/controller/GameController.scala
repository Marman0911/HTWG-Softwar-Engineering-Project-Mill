package controller

import model.board.Board
import model.board.Position
import model.game.GameState

import scala.util.{Failure, Success, Try}

trait GameObserver:
  def update(): Unit

trait Observable:
  private var _observers: List[GameObserver] =
    List.empty

  def addObserver(observer: GameObserver): Unit =
    _observers = observer :: _observers

  def removeObserver(observer: GameObserver): Unit =
    _observers = _observers.filterNot(_ eq observer)

  protected def notifyObservers(): Unit =
    _observers.foreach(_.update())

final case class GameException(message: String) extends Exception(message)

class GameController(initialState: GameState = GameState()) extends Observable:

  private var state: GameState =
    initialState

  private var history: List[GameState] =
    Nil

  private var phase: GamePhase =
    PlacingPhase(parseInput)

  def isGameOver: Boolean =
    !shouldContinue(state)

  def boardViewModel: BoardViewModel =
    BoardViewMapper.toViewModel(state)

  def currentPrompt: String =
    phase.prompt(state)

  def handleInput(input: String): Try[Unit] =
    input.trim.toLowerCase match
      case "undo" =>
        undo()

      case _ =>
        phase.handleInput(input, state) match
          case Left(message) =>
            Failure(GameException(message))

          case Right(nextState) =>
            history = state :: history
            state = nextState
            phase = phase.next(state)
            notifyObservers()
            Success(())

  def welcomeMessage: String =
    GameMessages.welcomeMessage

  def undo(): Try[Unit] =
    history match
      case Nil =>
        Failure(GameException("Nothing to undo."))

      case prev :: rest =>
        state = prev
        history = rest
        notifyObservers()
        Success(())

  private[controller] def shouldContinue(state: GameState): Boolean =
    !state.player1.hasLost && !state.player2.hasLost

  private[controller] def reverseCoords(board: Board): Map[(Int, Int), Position] =
    board.allPositions.map(pos => board.posCoords(pos) -> pos).toMap

  private[controller] def parseInput(input: String, board: Board): Option[Position] =
    val clean =
      input.trim.toLowerCase.filter(_.isLetterOrDigit)

    val parsedInput =
      clean.toList match
        case letter :: digit :: Nil if letter.isLetter && digit.isDigit =>
          Some((letter, digit.asDigit))

        case digit :: letter :: Nil if digit.isDigit && letter.isLetter =>
          Some((letter, digit.asDigit))

        case _ =>
          None

    parsedInput.flatMap: (letter, rowNumber) =>
      val row =
        rowNumber - 1

      val col =
        letter - 'a'

      reverseCoords(board).get((row * 2, col * 5))

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