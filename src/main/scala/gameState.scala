enum MoveType:
  case Place      // Stein setzen
  case Move       // Stein bewegen
  case Fly        // Stein springen lassen
  case Remove     // gegnerischen Stein entfernen


case class BoardPosition(ring: Int, point: Int):
  require(ring >= 0, "ring must be >= 0")
  require(point >= 0 && point < 8, "point must be between 0 and 7")


case class Move(
    player: PlayerId,
    moveType: MoveType,
    from: Option[BoardPosition],
    to: Option[BoardPosition],
    remove: Option[BoardPosition] = None
):
  def isPlaceMove: Boolean =
    moveType == MoveType.Place && from.isEmpty && to.nonEmpty

  def isMoveMove: Boolean =
    moveType == MoveType.Move && from.nonEmpty && to.nonEmpty

  def isFlyMove: Boolean =
    moveType == MoveType.Fly && from.nonEmpty && to.nonEmpty

  def isRemoveMove: Boolean =
    moveType == MoveType.Remove && from.isEmpty && to.isEmpty && remove.nonEmpty

  def isValidShape: Boolean =
    moveType match
      case MoveType.Place  => isPlaceMove
      case MoveType.Move   => isMoveMove
      case MoveType.Fly    => isFlyMove
      case MoveType.Remove => isRemoveMove
