package tui

import controller.GameController
import scala.io.StdIn.readLine
import view.BoardView

// The TUI is the View layer for keyboard/terminal modality.
// It translates user keystrokes into modality-independent controller actions,
// drives the game loop, and re-renders via the observer callback.
@main def millGame(): Unit =
  val controller = GameController()
  val view       = BoardView(controller) // View holds ref to Controller for active pull

  controller.addObserver(view)

  println(controller.welcomeMessage)
  println(view.renderWithCoords(controller.boardViewModel))

  while !controller.isGameOver do
    print(controller.currentPrompt)
    val input = readLine()
    controller.handleInput(input) match
      case Left(message) => println(message)
      case Right(_)      => () // board re-render is triggered via observer