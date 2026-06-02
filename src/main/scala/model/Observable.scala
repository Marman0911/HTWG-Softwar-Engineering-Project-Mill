package model

trait Observer:
  def update(state: GameState): Unit

trait Observable[T]:
  def observers: List[Observer]
  def addObserver(o: Observer): T

  protected def notifyObservers(state: GameState): Unit =
    observers.foreach(_.update(state))