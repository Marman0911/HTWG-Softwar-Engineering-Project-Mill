package view.tui

import controller.{IController, GameObserver}
import view.BoardView
import scala.util.{Success, Failure}

class TuiRunner(controller: IController, readLine: () => String) extends GameObserver:
  
  val view = BoardView(controller)
  controller.addObserver(this)

  override def update(): Unit =
    println("\n--- Update ---")
    println(view.renderWithCoords(controller.boardViewModel))
    print(controller.currentPrompt)

  def run(): Unit =
    println(controller.welcomeMessage)
    println(view.renderWithCoords(controller.boardViewModel))
    
    while !controller.isGameOver do
      print(controller.currentPrompt)
      val input = readLine()
      
      if input != null then
        input.trim.toLowerCase match
          case "undo" =>
            controller.undo() match
              case Failure(exception) => println(exception.getMessage)
              case Success(_)         => ()
              
          case other =>
            controller.handleInput(input) match
              case Failure(exception) => println(exception.getMessage)
              case Success(_)         => ()