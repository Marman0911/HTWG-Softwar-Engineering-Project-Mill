package app

import controller.GameController
import gui.MillGui
import model.game.GameComponent
import scala.io.StdIn.readLine
import tui.TuiRunner

object MillApp:

  def createController(): GameController =
    GameController(GameComponent.standard)

  def createTuiThread(
      controller: GameController,
      readInput: () => String
  ): Thread =
    val tuiThread =
      new Thread(new Runnable:
        override def run(): Unit =
          TuiRunner(controller, readInput).run()
      )

    tuiThread.setDaemon(true)
    tuiThread

  def start(
      controller: GameController,
      tuiThread: Thread,
      startGui: GameController => Unit
  ): Unit =
    tuiThread.start()
    startGui(controller)

  def startApp(
      readInput: () => String = () => readLine(),
      startGui: GameController => Unit = MillGui.startWith,
      createThread: (GameController, () => String) => Thread = createTuiThread
  ): Unit =
    val controller =
      createController()

    val tuiThread =
      createThread(controller, readInput)

    start(controller, tuiThread, startGui)

@main def millApp(): Unit =
  MillApp.startApp()