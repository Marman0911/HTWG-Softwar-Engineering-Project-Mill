package controller

import com.google.inject.{AbstractModule, Provider}
import model.fileio.*

class GameControllerProvider extends Provider[IController]:
  override def get(): IController = GameController()

class GameModule extends AbstractModule:
  override def configure(): Unit =
    bind(classOf[IController]).toProvider(classOf[GameControllerProvider])
    bind(classOf[FileIOInterface]).to(classOf[JsonFileIO]) //or XmlFileIO