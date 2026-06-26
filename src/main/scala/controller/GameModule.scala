package controller

import com.google.inject.AbstractModule

class GameModule extends AbstractModule {
  override def configure(): Unit = {
    bind(classOf[IController]).to(classOf[GameController])
  }
}