package TUI

import controller.GameController
import model.GameState
import scala.io.StdIn.readLine
import view.BoardView
import view.BoardViewMapper

@main def millGame(): Unit =
  val view = BoardView()
  val state = GameState()

  GameController.addObserver(view)

  println(GameController.welcomeMessage)
  println(view.renderWithCoords(BoardViewMapper.toViewModel(state)))

  GameController.runGameLoop(state, () => readLine(), print, msg => println(msg))