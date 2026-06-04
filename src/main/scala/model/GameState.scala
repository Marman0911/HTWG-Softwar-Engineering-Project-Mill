package model

enum MoveType:
  case Place, Move, Fly, Remove

case class GameState(
  board: MillBoard,
  player1: Player,
  player2: Player,
  currentPlayer: PlayerId = PlayerId.One,
  observers: List[Observer] = List.empty
) extends Observable[GameState]:

  def addObserver(o: Observer): GameState =
    copy(observers = o :: observers)

  def currentPlayerObj: Player =
    if currentPlayer == PlayerId.One then player1 else player2

  private def nextPlayer: PlayerId =
    if currentPlayer == PlayerId.One then PlayerId.Two else PlayerId.One

  def placeStone(pos: Position): Option[GameState] =
    board.placeStone(pos, currentPlayer).map: newBoard =>
      val newState = copy(
        board = newBoard,
        currentPlayer = nextPlayer,
        observers = observers
      )
      newState.notifyObservers(newState)
      newState

object GameState:
  def apply(): GameState =
    GameState(
      MillBoard(),
      PlayerFactory.create(PlayerId.One),
      PlayerFactory.create(PlayerId.Two)
    )