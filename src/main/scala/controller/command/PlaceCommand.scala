package controller.command

// WICHTIG: Das sorgt dafür, dass PlaceCommand das GameState-Trait überhaupt kennt!
import model.game.GameState 
import model.board.Position
import scala.util.{Try, Success, Failure}

case class PlaceCommand(pos: Position) extends GameCommand:

  override def execute(state: GameState): Try[GameState] =
    state.placeStone(pos) match
      case Some(newState) => Success(newState)
      case None           => Failure(new Exception("Feld ist bereits belegt oder Zug ungültig!"))

  override def undo(state: GameState): Try[GameState] =
    state.removeStone(pos) match
      case Some(newState) => Success(newState)
      case None           => Failure(new Exception("Konnte Stein zum Rückgängigmachen nicht entfernen!"))