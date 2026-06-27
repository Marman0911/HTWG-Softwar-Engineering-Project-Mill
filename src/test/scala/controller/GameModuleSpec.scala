package controller

import com.google.inject.Guice
import org.scalatest.matchers.should.Matchers
import org.scalatest.wordspec.AnyWordSpec
import scala.util.{Success, Failure}

class GameModuleSpec extends AnyWordSpec with Matchers:

  "GameModule" should {

    "create an injector without errors" in {
      val injector = Guice.createInjector(new GameModule())
      injector should not be null
    }

    "bind IController to GameController" in {
      val injector = Guice.createInjector(new GameModule())
      val controller = injector.getInstance(classOf[IController])
      controller shouldBe a[GameController]
    }

    "create a fresh controller that is not game over" in {
      val injector = Guice.createInjector(new GameModule())
      val controller = injector.getInstance(classOf[IController])
      controller.isGameOver.should(be(false))
    }

    "create a controller with a valid welcome message" in {
      val injector = Guice.createInjector(new GameModule())
      val controller = injector.getInstance(classOf[IController])
      controller.welcomeMessage.should(not(be(empty)))
    }

    "create a controller that accepts valid input" in {
      val injector = Guice.createInjector(new GameModule())
      val controller = injector.getInstance(classOf[IController])
      controller.handleInput("a1").should(be(Success(())))
    }

    "create independent instances per injection" in {
      val injector = Guice.createInjector(new GameModule())
      val controller1 = injector.getInstance(classOf[IController])
      val controller2 = injector.getInstance(classOf[IController])
      controller1.handleInput("a1")
      controller2.boardViewModel.stones.should(be(empty))
    }

  }