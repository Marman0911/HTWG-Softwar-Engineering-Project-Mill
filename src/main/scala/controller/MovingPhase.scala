package controller

import controller.command.GameCommand
import controller.command.MoveCommand
import model.board.Board
import model.board.Position
import model.game.GameState
import model.player.PlayerId

import scala.util.{Failure, Success, Try}

class MovingPhase(parsePos: (String, Board) => Option[Position]) extends GamePhase:

  def handleInput(input: String, state: GameState): Try[GameCommand] =
    val parts =
      input.trim.split("\\s+").filter(_.nonEmpty)

    if parts.length != 2 then
      Failure(new IllegalArgumentException(GameMessages.invalidMove))
    else
      val from =
        parsePos(parts(0), state.board)

      val to =
        parsePos(parts(1), state.board)

      (from, to) match
        case (Some(start), Some(target)) =>
          Success(MoveCommand(start, target))

        case _ =>
          Failure(new IllegalArgumentException(GameMessages.invalidMove))

  def prompt(state: GameState): String =
    val playerNumber =
      if state.currentPlayer == PlayerId.One then 1 else 2

    s"Player $playerNumber move: start target (e.g. a1 d1): "

  def next(state: GameState): GamePhase =
    this
