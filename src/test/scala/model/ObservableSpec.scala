package model

import org.scalatest.matchers.should.Matchers
import org.scalatest.wordspec.AnyWordSpec

class ObservableSpec extends AnyWordSpec with Matchers:

  private class RecordingObserver extends Observer:
    var updateCalls = 0
    var lastState: Option[GameState] = None

    def update(state: GameState): Unit =
      updateCalls = updateCalls + 1
      lastState = Some(state)

  private class TestObservable extends Observable

  "Observable" should {

    "notify added observers with the provided state" in {
      val observable = TestObservable()
      val observer = RecordingObserver()
      val state = GameState()

      observable.addObserver(observer)
      observable.notifyObservers(state)

      observer.updateCalls should be(1)
      observer.lastState should be(Some(state))
    }

    "notify all observers" in {
      val observable = TestObservable()
      val observer1 = RecordingObserver()
      val observer2 = RecordingObserver()
      val state = GameState()

      observable.addObserver(observer1)
      observable.addObserver(observer2)
      observable.notifyObservers(state)

      observer1.updateCalls should be(1)
      observer2.updateCalls should be(1)
    }
  }
