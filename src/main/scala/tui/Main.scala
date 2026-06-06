package tui

import controller.GameController
import scala.io.StdIn.readLine
import view.BoardView

// TuiRunner is the testable part of the TUI – the @main wires real I/O into it.
class TuiRunner(controller: GameController, readInput: () => String):
  val view = BoardView(controller)
  controller.addObserver(view)

  def run(): Unit =
    println(controller.welcomeMessage)
    println(view.renderWithCoords(controller.boardViewModel))

    while !controller.isGameOver do
      print(controller.currentPrompt)
      val input = readInput()
      controller.handleInput(input) match
        case Left(message) => println(message)
        case Right(_)      => () // board re-render is triggered via observer

@main def millGame(): Unit =
  TuiRunner(GameController(), () => readLine()).run()