package model

enum MoveType:
  case Place, Move, Fly, Remove

case class GameState(
  board: MillBoard,
  player1: Player,
  player2: Player,
  currentPlayer: PlayerId = PlayerId.One
) extends Observable:

  def currentPlayerObj: Player =
    if currentPlayer == PlayerId.One then player1 else player2

  def placeStone(pos: Position): Option[GameState] =
    board.placeStone(pos, currentPlayer).map: newBoard =>
      val next = if currentPlayer == PlayerId.One then PlayerId.Two else PlayerId.One
      val newState = copy(board = newBoard, currentPlayer = next)
      newState.notifyObservers(newState)
      newState

object GameState:
  def apply(): GameState =
    GameState(MillBoard(), Player(PlayerId.One), Player(PlayerId.Two))