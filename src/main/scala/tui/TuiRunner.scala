package tui

import controller.GameController
import scala.io.StdIn.readLine
import view.BoardView
import model.game.GameComponent
import scala.util.{Success, Failure} // NEU: Import für die Try-Monade

class TuiRunner(controller: GameController, readInput: () => String):
  val view = BoardView(controller)
  controller.addObserver(view)

  def run(): Unit =
    println(controller.welcomeMessage)
    println(view.renderWithCoords(controller.boardViewModel))
    while !controller.isGameOver do
      print(controller.currentPrompt)
      val input = readInput()
      
      input.trim.toLowerCase match
        case "undo" =>
          // NEU: controller.undo liefert jetzt ein Try
          controller.undo() match
            case Failure(exception) => println(exception.getMessage)
            case Success(_)         => () // Alles super, Observer aktualisiert die View automatisch
            
        case other => // FIX: Der leere Case-Pfeil wurde repariert
          // NEU: controller.handleInput liefert jetzt ein Try
          controller.handleInput(input) match
            case Failure(exception) => println(exception.getMessage)
            case Success(_)         => () // Alles super