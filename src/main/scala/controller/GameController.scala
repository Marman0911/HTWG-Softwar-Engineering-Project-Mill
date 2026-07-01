package controller

import controller.command.{GameCommand, MoveCommand, PlaceCommand, RemoveCommand}
import model.board.{Board, Position}
import model.fileio.*
import model.game.{GameState, MillRules}

import scala.util.{Failure, Success, Try}
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter

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

class GameController(initialState: GameState, fileIO: FileIOInterface) extends IController:

  private var state: GameState = initialState
  private var history: List[GameCommand] = Nil
  private var phase: GamePhase = phaseFor(initialState)

  private def phaseFor(gameState: GameState): GamePhase =
    if gameState.player1.stonesInHand > 0 ||
        gameState.player2.stonesInHand > 0 then
      PlacingPhase(parseInput)
    else
      MovingPhase(parseInput)

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
          case Failure(exception) =>
            Failure(GameException(exception.getMessage))

          case Success(command: GameCommand) =>
            val stateBeforeCommand =
              state

            command.execute(state) match
              case Failure(exception) =>
                Failure(exception)

              case Success(nextState) =>
                history = command :: history
                state = nextState
                phase =
                  phaseAfter(command, stateBeforeCommand, nextState)

                notifyObservers()
                Success(())

  private def phaseAfter(
      command: GameCommand,
      stateBeforeCommand: GameState,
      stateAfterCommand: GameState
  ): GamePhase =
    command match
      case PlaceCommand(pos)
          if MillRules.formsMillAt(
            stateAfterCommand.board,
            pos,
            stateBeforeCommand.currentPlayer
          ) =>
        new RemovingPhase(parseInput)

      case MoveCommand(_, to)
          if MillRules.formsMillAt(
            stateAfterCommand.board,
            to,
            stateBeforeCommand.currentPlayer
          ) =>
        new RemovingPhase(parseInput)

      case _ =>
        phase.next(stateAfterCommand)

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
              command match
                // Wenn das Entfernen rückgängig gemacht wird,
                // darf derselbe Spieler wieder einen Stein entfernen.
                case _: RemoveCommand =>
                  new RemovingPhase(parseInput)

                case _ =>
                  phaseFor(state)

            notifyObservers()
            Success(())

  private[controller] def shouldContinue(state: GameState): Boolean =
    !state.player1.hasLost && !state.player2.hasLost &&
      !currentPlayerIsBlocked(state)

  private def currentPlayerIsBlocked(state: GameState): Boolean =
    val inMovePhase =
      state.player1.stonesInHand == 0 && state.player2.stonesInHand == 0
    if !inMovePhase then return false

    val board       = state.board
    val currentId   = state.currentPlayer
    val myPositions = board.placedStones.collect { case (pos, id) if id == currentId => pos }
    val freePos     = board.allPositions.filterNot(board.placedStones.contains)

    if state.currentPlayerObj.canFly then
      freePos.isEmpty
    else
      !myPositions.exists(pos => board.neighbours(pos).exists(n => !board.placedStones.contains(n)))

  private[controller] def reverseCoords(board: Board): Map[(Int, Int), Position] =
    board.allPositions.map(pos => board.posCoords(pos) -> pos).toMap

  private[controller] def parseInput(input: String, board: Board): Option[Position] =
    val clean =
      input.trim.toLowerCase.filter(c => c.isLetter || c.isDigit)

    if clean.length < 2 then None
    else
      val (letter, number) =
        if clean.head.isLetter then (clean.head, clean.tail)
        else (clean.last, clean.init)

      val colIdx =
        letter - 'a'

      number.toIntOption match
        case None =>
          None

        case Some(rowNum) =>
          reverseCoords(board).get((rowNum - 1) * 2, colIdx * 5)

  def saveGame(customName: String): Try[Unit] =
    val sanitized =
      customName.trim

    val fileName =
      if sanitized.isEmpty then
        val timestamp =
          LocalDateTime.now().format(
            DateTimeFormatter.ofPattern("yyyyMMdd_HHmm")
          )
        s"millbc_$timestamp"
      else
        sanitized

    val extension =
      if fileIO.isInstanceOf[model.fileio.JsonFileIO] then ".json" else ".xml"

    val filePath =
      s"saves/$fileName$extension"

    scala.util.Try(fileIO.save(state, filePath))

  def loadGame(fileName: String): Try[Unit] =
    val filePath =
      s"saves/$fileName"

    fileIO.load(filePath) match
      case scala.util.Success(loadedState) =>
        state = loadedState

        phase =
          phaseFor(state)

        notifyObservers()
        scala.util.Success(())

      case scala.util.Failure(exception) =>
        Console.err.println(
          s"Fehler beim Laden des Spielstands: ${exception.getMessage}"
        )
        scala.util.Failure(exception)

object GameController:
  def apply(): GameController =
    new GameController(GameState(), new JsonFileIO())

  def apply(state: GameState): GameController =
    new GameController(state, new JsonFileIO())
