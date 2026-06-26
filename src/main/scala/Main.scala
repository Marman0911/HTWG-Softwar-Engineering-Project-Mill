package main

import controller.{IController, GameController, GameModule} // NEU: Interfaces und das Guice-Modul importieren
import view.tui.TuiRunner
import scala.io.StdIn.readLine
import view.BoardView
import model.game.GameComponent
import scala.util.{Success, Failure}
import com.google.inject.Guice // NEU: Google Guice importieren

// KORREKTUR: Der TuiRunner fordert jetzt das Interface IController an (Folie 3 & 7)
class TuiRunner(controller: IController, readInput: () => String):
  // Hinweis: Stelle sicher, dass auch in der Klasse 'BoardView' im Konstruktor 'IController' steht!
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
          controller.undo() match
            case Failure(exception) => println(exception.getMessage)
            case Success(_)         => () // Alles super, Observer aktualisiert die View automatisch
            
        case other => 
          controller.handleInput(input) match
            case Failure(exception) => println(exception.getMessage)
            case Success(_)         => () // Alles super

// NEU: Der tatsächliche Startpunkt deines Programms (Folie 15: "Create an Injector in your main")
@main def main(): Unit =
  // 1. Wir erstellen den Guice-Injector mithilfe unseres GameModules
  val injector = Guice.createInjector(new GameModule)
  
  // 2. Wir holen uns die Instanz des Controllers über das Interface (Guice reicht uns automatisch den GameController rein)
  val controller = injector.getInstance(classOf[IController])
  
  // 3. Wir übergeben den injizierten Controller an den TuiRunner
  val tuiRunner = new TuiRunner(controller, () => readLine())
  
  // 4. Spiel starten!
  tuiRunner.run()