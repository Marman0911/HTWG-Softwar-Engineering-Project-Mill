case class WorksheetPosition(ring: Int, slot: Int)

enum WorksheetPlayer:
  case One, Two

case class WorksheetStone(owner: WorksheetPlayer)

val position = WorksheetPosition(0, 3)
val stone = WorksheetStone(WorksheetPlayer.One)

position.ring
position.slot
stone.owner