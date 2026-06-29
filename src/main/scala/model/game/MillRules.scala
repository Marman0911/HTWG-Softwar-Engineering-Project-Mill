package model.game

import model.board.{Board, Position}
import model.player.PlayerId

object MillRules:

  private def otherPlayer(player: PlayerId): PlayerId =
    player match
      case PlayerId.One => PlayerId.Two
      case PlayerId.Two => PlayerId.One

  /*
   * Liefert alle möglichen Dreierreihen auf dem Spielbrett.
   *
   * Auf jedem Ring:
   * oben, rechts, unten, links
   *
   * Zusätzlich:
   * die vier Linien zwischen den drei Ringen.
   */
  def allMills(board: Board): Seq[Seq[Position]] =
    val ringCount =
      board.allPositions.map(_.ring).maxOption.map(_ + 1).getOrElse(0)

    val millsOnRings =
      (0 until ringCount).flatMap: ring =>
        Seq(
          Seq(Position(ring, 0), Position(ring, 1), Position(ring, 2)),
          Seq(Position(ring, 2), Position(ring, 3), Position(ring, 4)),
          Seq(Position(ring, 4), Position(ring, 5), Position(ring, 6)),
          Seq(Position(ring, 6), Position(ring, 7), Position(ring, 0))
        )

    // Bei weniger als drei Ringen ist der Bereich automatisch leer.
    val millsBetweenRings =
      (0 until (ringCount - 2)).flatMap: firstRing =>
        Seq(1, 3, 5, 7).map: slot =>
          Seq(
            Position(firstRing, slot),
            Position(firstRing + 1, slot),
            Position(firstRing + 2, slot)
          )

    millsOnRings ++ millsBetweenRings

  /*
   * Alle vollständigen Mühlen eines bestimmten Spielers.
   */
  def millsOf(board: Board, player: PlayerId): Seq[Seq[Position]] =
    allMills(board).filter: mill =>
      mill.forall: position =>
        board.placedStones.get(position).contains(player)

  /*
   * Prüft, ob diese konkrete Position Teil einer vollständigen Mühle ist.
   */
  def isPartOfMill(
      board: Board,
      position: Position,
      player: PlayerId
  ): Boolean =
    millsOf(board, player).exists(_.contains(position))

  /*
   * Prüft nach einem gesetzten oder bewegten Stein,
   * ob genau dieser Zielpunkt eine Mühle vervollständigt.
   */
  def formsMillAt(
      board: Board,
      position: Position,
      player: PlayerId
  ): Boolean =
    isPartOfMill(board, position, player)

  /*
   * Welche gegnerischen Steine dürfen entfernt werden?
   *
   * Regel:
   * - Steine außerhalb einer Mühle müssen zuerst genommen werden.
   * - Nur wenn alle gegnerischen Steine in Mühlen sind,
   *   darf auch ein Stein aus einer Mühle entfernt werden.
   */
  def removableOpponentPositions(
      board: Board,
      currentPlayer: PlayerId
  ): Set[Position] =
    val opponent =
      otherPlayer(currentPlayer)

    val opponentPositions =
      board.placedStones.collect:
        case (position, owner) if owner == opponent => position
      .toSet

    val opponentStonesOutsideMills =
      opponentPositions.filter: position =>
        !isPartOfMill(board, position, opponent)

    if opponentStonesOutsideMills.nonEmpty then
      opponentStonesOutsideMills
    else
      opponentPositions