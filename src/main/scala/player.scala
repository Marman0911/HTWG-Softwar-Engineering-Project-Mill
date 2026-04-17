enum PlayerId:
    case One, Two

case class Player(
    id: PlayerId,
    stonesInHand: Int = 9,
    stonesOnBoard: Int = 0,
):

    def totalStones: Int = stonesInHand + stonesOnBoard

    def hasLost: Boolean = totalStones < 3

    def canFly: Boolean = stonesOnBoard == 3 && stonesInHand == 0

