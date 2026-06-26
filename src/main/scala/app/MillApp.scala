package app

import controller.GameController
import gui.MillGui
import model.game.GameComponent
import scala.io.StdIn.readLine
import tui.TuiRunner

@main def millApp(): Unit =
  // Hier wird genau ein gemeinsamer Controller erstellt.
  val controller =
    GameController(GameComponent.standard)

  // Die TUI erhält denselben Controller.
  val tuiRunner =
    TuiRunner(controller, () => readLine())

  // Die TUI läuft parallel, da readLine() auf Eingaben wartet.
  val tuiThread =
    new Thread(new Runnable:
      override def run(): Unit =
        tuiRunner.run()
    )

  // Beim Schließen der GUI darf der Hintergrund-Thread automatisch enden.
  tuiThread.setDaemon(true)
  tuiThread.start()

  // Die GUI erhält exakt denselben Controller und öffnet zuerst das Menü.
  val gui =
    new MillGui(controller)

  gui.start()