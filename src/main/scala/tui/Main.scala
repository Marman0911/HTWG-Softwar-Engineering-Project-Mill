package tui

import controller.GameController
import controller.GameObserver
import model.game.GameComponent
import scala.io.StdIn.readLine
import view.BoardView

// TuiRunner ist ein GameObserver.
// Dadurch kann die TUI vom Controller informiert werden,
// wenn sich der Spielzustand ändert.
class TuiRunner(controller: GameController, readInput: () => String) extends GameObserver:

  private val view =
    BoardView(controller)

  // Hier bekommt die TUI den Controller über den Konstruktor:
  // class TuiRunner(controller: GameController, ...)
  //
  // Der Controller wird in der TUI gespeichert und von der BoardView benutzt.
  // Dadurch kann die TUI den aktuellen Spielzustand anzeigen.
  //
  // Hier registriert sich TuiRunner selbst beim Controller als Observer.
  controller.addObserver(this)

  // Wenn der Controller seine Observer benachrichtigt,
  // wird diese update-Methode aufgerufen.
  override def update(): Unit =
    view.update()

  def run(): Unit =
    println(controller.welcomeMessage)
    println(view.renderWithCoords(controller.boardViewModel))

    while !controller.isGameOver do
      print(controller.currentPrompt)

      val input =
        readInput()

      // Die TUI verändert das Spiel nicht selbst.
      // Sie gibt die Eingabe nur an den Controller weiter.
      // Auch "undo" wird im Controller behandelt.
      controller.handleInput(input) match
        case Left(message) =>
          println(message)

        case Right(_) =>
          ()

// Diese Main-Methode startet nur die TUI alleine.
// Hier wird ein Controller erstellt und an TuiRunner übergeben.
@main def millGame(): Unit =
  val controller =
    GameController(GameComponent.standard)

  // Hier bekommt die TUI den Controller.
  TuiRunner(controller, () => readLine()).run()