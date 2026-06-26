package controller

import model.game.GameState
import model.board.Board
import model.board.Position
import controller.command.GameCommand
import scala.util.{Try, Success, Failure}
import com.google.inject.Inject

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

final case class GameException(message: String) extends Exception(message)

class GameController @Inject() () extends IController:

  private var state: GameState = GameState()
  private var history: List[GameCommand] = Nil
  private var phase: GamePhase = PlacingPhase(parseInput)

  def isGameOver: Boolean = !shouldContinue(state)

  def boardViewModel: BoardViewModel = BoardViewMapper.toViewModel(state)

  def currentPrompt: String = phase.prompt(state)

  def handleInput(input: String): Try[Unit] =
    input.trim.toLowerCase match
      case "undo" => undo()
      case _ =>
        phase.handleInput(input, state) match
          // FIX: Aus Left(message) wird Failure(exception)
          case Failure(exception) => 
            Failure(GameException(exception.getMessage))
          // FIX: Aus Right(command) wird Success(command)
          case Success(command: GameCommand) =>
            command.execute(state) match
              case Failure(exception) => 
                Failure(exception)
              case Success(nextState) =>
                history = command :: history
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
      case command :: rest =>
        command.undo(state) match
          case Failure(exception) => 
            Failure(exception)
          case Success(prevState) =>
            state = prevState
            history = rest
            phase =
              if state.player1.stonesInHand > 0 || state.player2.stonesInHand > 0 then
                PlacingPhase(parseInput)
              else
                MovingPhase(parseInput)
            notifyObservers()
            Success(())

  private[controller] def shouldContinue(state: GameState): Boolean =
    !state.player1.hasLost && !state.player2.hasLost

  private[controller] def reverseCoords(board: Board): Map[(Int, Int), Position] =
    board.allPositions.map(pos => board.posCoords(pos) -> pos).toMap

  private[controller] def parseInput(input: String, board: Board): Option[Position] =
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