/* 
diese Klasse erstellt einen GameController
Es startet die TUI mit diesem Controller
Es startet die GUI mit demselben Controller
 */



package app

import controller.GameController
import gui.MillGui
import model.GameFactory
import scala.io.StdIn.readLine
import tui.TuiRunner

@main def millApp(): Unit =
  val controller = GameController(GameFactory.standard)

  val tuiThread = new Thread(new Runnable:
    override def run(): Unit =
      TuiRunner(controller, () => readLine()).run()
  )

  tuiThread.setDaemon(true)
  tuiThread.start()

  MillGui.startWith(controller)