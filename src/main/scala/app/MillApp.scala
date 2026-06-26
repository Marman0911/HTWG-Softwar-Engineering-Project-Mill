package app

import controller.{IController, GameModule}
import view.tui.TuiRunner
import view.gui.MillGui 
import scala.io.StdIn.readLine
import com.google.inject.Guice

@main def millApp(): Unit =
  // 1. Guice baut den Controller
  val injector = Guice.createInjector(new GameModule)
  val controller = injector.getInstance(classOf[IController])

  // 2. TUI kriegt den IController
  val tuiRunner = TuiRunner(controller, () => readLine())
  val tuiThread = new Thread(new Runnable:
    override def run(): Unit = tuiRunner.run()
  )
  tuiThread.setDaemon(true)
  tuiThread.start()

  // 3. GUI ist jetzt eine Class und kriegt auch den IController
  val gui = new MillGui(controller)
  gui.start()