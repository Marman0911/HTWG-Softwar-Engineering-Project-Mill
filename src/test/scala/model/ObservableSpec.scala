package controller

import org.scalatest.matchers.should.Matchers
import org.scalatest.wordspec.AnyWordSpec

class ObservableSpec extends AnyWordSpec with Matchers:

  private class RecordingObserver extends GameObserver:
    var updateCalls = 0

    def update(): Unit =
      updateCalls = updateCalls + 1

  private class TestObservable extends Observable:
    def trigger(): Unit = notifyObservers()

  "Observable" should {

    "notify added observers" in {
      val observable = TestObservable()
      val observer   = RecordingObserver()

      observable.addObserver(observer)
      observable.trigger()

      observer.updateCalls should be(1)
    }

    "notify all observers" in {
      val observable = TestObservable()
      val observer1  = RecordingObserver()
      val observer2  = RecordingObserver()

      observable.addObserver(observer1)
      observable.addObserver(observer2)
      observable.trigger()

      observer1.updateCalls should be(1)
      observer2.updateCalls should be(1)
    }

    "not notify a removed observer" in {
      val observable = TestObservable()
      val observer   = RecordingObserver()

      observable.addObserver(observer)
      observable.removeObserver(observer)
      observable.trigger()

      observer.updateCalls should be(0)
    }
  }
