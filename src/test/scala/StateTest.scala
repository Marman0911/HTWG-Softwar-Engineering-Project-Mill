import org.scalatest.wordspec.AnyWordSpec
import org.scalatest.matchers.should.Matchers

class StateTest extends AnyWordSpec with Matchers:

  "BoardPosition" should {

    "accept valid positions" in {
      val pos1 = BoardPosition(0, 0)
      pos1.ring should be(0)
      pos1.point should be(0)

      val pos2 = BoardPosition(2, 7)
      pos2.ring should be(2)
      pos2.point should be(7)
    }

    "reject invalid ring" in {
      an [IllegalArgumentException] should be thrownBy BoardPosition(-1, 0)
      an [IllegalArgumentException] should be thrownBy BoardPosition(3, 0)
    }

    "reject invalid point" in {
      an [IllegalArgumentException] should be thrownBy BoardPosition(0, -1)
      an [IllegalArgumentException] should be thrownBy BoardPosition(0, 8)
    }
  }

  "Move" should {

    "validate a place move" in {
      val move = Move(
        player = PlayerId.One,
        moveType = MoveType.Place,
        from = None,
        to = Some(BoardPosition(0, 3))
      )

      move.isPlaceMove should be(true)
      move.isValidShape should be(true)
    }

    "reject an invalid place move" in {
      val move = Move(
        player = PlayerId.One,
        moveType = MoveType.Place,
        from = Some(BoardPosition(0, 1)),
        to = Some(BoardPosition(0, 3))
      )

      move.isPlaceMove should be(false)
      move.isValidShape should be(false)
    }

    "validate a move move" in {
      val move = Move(
        player = PlayerId.One,
        moveType = MoveType.Move,
        from = Some(BoardPosition(0, 1)),
        to = Some(BoardPosition(0, 2))
      )

      move.isMoveMove should be(true)
      move.isValidShape should be(true)
    }

    "reject an invalid move move" in {
      val move = Move(
        player = PlayerId.One,
        moveType = MoveType.Move,
        from = None,
        to = Some(BoardPosition(0, 2))
      )

      move.isMoveMove should be(false)
      move.isValidShape should be(false)
    }

    "validate a fly move" in {
      val move = Move(
        player = PlayerId.One,
        moveType = MoveType.Fly,
        from = Some(BoardPosition(1, 1)),
        to = Some(BoardPosition(2, 2))
      )

      move.isFlyMove should be(true)
      move.isValidShape should be(true)
    }

    "reject an invalid fly move" in {
      val move = Move(
        player = PlayerId.One,
        moveType = MoveType.Fly,
        from = Some(BoardPosition(1, 1)),
        to = None
      )

      move.isFlyMove should be(false)
      move.isValidShape should be(false)
    }

    "validate a remove move" in {
      val move = Move(
        player = PlayerId.One,
        moveType = MoveType.Remove,
        from = None,
        to = None,
        remove = Some(BoardPosition(2, 5))
      )

      move.isRemoveMove should be(true)
      move.isValidShape should be(true)
    }

    "reject an invalid remove move" in {
      val move = Move(
        player = PlayerId.One,
        moveType = MoveType.Remove,
        from = None,
        to = None,
        remove = None
      )

      move.isRemoveMove should be(false)
      move.isValidShape should be(false)
    }
  }