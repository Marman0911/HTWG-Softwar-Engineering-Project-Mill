package tui

import controller.GameController
import controller.GameObserver
import scala.io.StdIn.readLine
import view.BoardView
import model.game.GameComponent


//Tui Runner ist ein GameObserver
class TuiRunner(controller: GameController, readInput: () => String) extends GameObserver:

  private val view =
    BoardView(controller)

  //TuiRunner registriert sich selbst beim Controller als Observer
  controller.addObserver(this)

  override def update(): Unit =
    view.update()

  def run(): Unit =
    println(controller.welcomeMessage)
    println(view.renderWithCoords(controller.boardViewModel))

    while !controller.isGameOver do
      print(controller.currentPrompt)

      val input =
        readInput()

      input.trim.toLowerCase match
        case "undo" =>
          controller.undo() match
            case Left(msg) => println(msg)
            case Right(_)  => ()

        case _ =>
          controller.handleInput(input) match
            case Left(message) => println(message)
            case Right(_)      => ()

@main def millGame(): Unit =
  TuiRunner(GameController(GameComponent.standard), () => readLine()).run()