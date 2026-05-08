enum MoveType:                           // Enum-Liste von Möglichkeiten
  case Place      // Stein setzen
  case Move       // Stein bewegen
  case Fly        // Stein springen lassen
  case Remove     // gegnerischen Stein entfernen


case class BoardPosition(ring: Int, point: Int):
  require(ring >= 0 && ring < 3,
    "ring must be between 0 and 2") // Ring 0 Innen, 1 mitte, 2 außen

  require(require(point >= 0 && point < 8, // jedes Quadrat hat 8 Punkte 0 bis 7
    "point must be between 0 and 7"))


case class Move(                //Ein Zug besteht aus mehreren Informationen
    player: PlayerId,           //Wer macht den Zug, Player one, player two...
    moveType: MoveType,           //Welche Art Bewegung: Fly, place remove...
    from: Option[BoardPosition],   // Von wo kommt der Stein - option: vlt gibt es wert vlt nicht
    to: Option[BoardPosition],            // wohin
    remove: Option[BoardPosition] = None
):

  //passt der Zug überhaupt zusammen, Phase 1 stein kommt von der hand nicht von anderem Feld

  //stein setzen, Stein kommt von keinem Feld(Hand), Stein braucht Ziel(nonEmpty)
  //moveType == MoveType.Place "Ist der aktuelle moveType gleich Place?"
  def isPlaceMove: Boolean =
    moveType == MoveType.Place && from.isEmpty && to.nonEmpty 

  def isMoveMove: Boolean =
    moveType == MoveType.Move && from.nonEmpty && to.nonEmpty

  def isFlyMove: Boolean =
    moveType == MoveType.Fly && from.nonEmpty && to.nonEmpty

  def isRemoveMove: Boolean =
    moveType == MoveType.Remove && from.isEmpty && to.isEmpty && remove.nonEmpty


//Je nachdem welcher Zugtyp vorliegt,prüfe die passende Regel
  def isValidShape: Boolean =
    moveType match
      case MoveType.Place  => isPlaceMove  //Wenn Place → prüfe isPlaceMove
      case MoveType.Move   => isMoveMove    //Wenn Move → prüfe isMoveMove
      case MoveType.Fly    => isFlyMove       //Wenn Fly → prüfe isFlyMove
      case MoveType.Remove => isRemoveMove    //Wenn Remove → prüfe isRemoveMove
