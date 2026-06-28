package controller

import com.google.inject.{AbstractModule, Provider}

class GameControllerProvider extends Provider[IController]:
  override def get(): IController = GameController()

class GameModule extends AbstractModule:
  override def configure(): Unit =
    bind(classOf[IController]).toProvider(classOf[GameControllerProvider])