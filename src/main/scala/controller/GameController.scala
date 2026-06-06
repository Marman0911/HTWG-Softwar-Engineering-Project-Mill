package controller

import model.GameState
import model.MillBoard
import model.Position

// The View implements this trait and registers itself as observer on the Controller.
// The Controller is the Observable: it sends a parameter-less signal to observers after
// every successful move. The View then actively pulls the data it needs via controller.boardViewModel.
trait GameObserver:
  def update(): Unit

// Observable trait: belongs in the controller layer because the Controller is the Observable.
trait Observable:
  private var _observers: List[GameObserver] = List.empty

  def addObserver(observer: GameObserver): Unit =
    _observers = observer :: _observers

  def removeObserver(observer: GameObserver): Unit =
    _observers = _observers.filterNot(_ eq observer)

  protected def notifyObservers(): Unit =
    _observers.foreach(_.update())

// GameController is the Observable in the MVC architecture.
// It owns the game state machine, applies business logic, and notifies observers.
// It has NO knowledge of any view modality (no I/O callbacks, no view imports).
class GameController(initialState: GameState = GameState()) extends Observable:

  private var state: GameState = initialState

  // ---- Read-only state access (View pulls data from Controller) ------------

  def isGameOver: Boolean = !shouldContinue(state)

  // The Controller fetches data from the Model and provides it to the View as a DTO.
  // The View never touches the Model directly.
  def boardViewModel: BoardViewModel = BoardViewMapper.toViewModel(state)

  // Returns the prompt string for the current player - no Model type exposed to View.
  def currentPrompt: String = GameMessages.promptFor(state.currentPlayer)

  /** Process one input token (e.g. "a1"). Updates internal state and notifies
    * observers on success; returns Left with an error message on failure.
    */
  def handleInput(input: String): Either[String, Unit] =
    handleTurnInput(state, input) match
      case Left(message) =>
        Left(message)
      case Right(nextState) =>
        state = nextState
        notifyObservers()
        Right(())

  // ---- UI text helpers (modality-independent strings) ---------------------

  def welcomeMessage: String =
    GameMessages.welcomeMessage

  // ---- Business logic (package-private for unit tests) --------------------

  private[controller] def shouldContinue(state: GameState): Boolean =
    !state.player1.hasLost && !state.player2.hasLost

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