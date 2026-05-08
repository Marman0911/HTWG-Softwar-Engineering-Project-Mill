package model

trait Observer:
  def update(state: GameState): Unit

trait Observable:
  private var observers: List[Observer] = List()
  def addObserver(o: Observer): Unit =
    observers = o :: observers
  def notifyObservers(state: GameState): Unit =
    observers.foreach(_.update(state))