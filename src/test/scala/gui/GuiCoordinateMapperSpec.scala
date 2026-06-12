package gui

import org.scalatest.wordspec.AnyWordSpec
import org.scalatest.matchers.should.Matchers

class GuiCoordinateMapperSpec extends AnyWordSpec with Matchers:

  "GuiCoordinateMapper.toPosition" should {

    "convert top left point to a1" in {
      GuiCoordinateMapper.toPosition(0, 0) shouldBe "a1"
    }

    "convert top middle point to d1" in {
      GuiCoordinateMapper.toPosition(3, 0) shouldBe "d1"
    }

    "convert bottom right point to g7" in {
      GuiCoordinateMapper.toPosition(6, 6) shouldBe "g7"
    }

    "convert middle point to d4" in {
      GuiCoordinateMapper.toPosition(3, 3) shouldBe "d4"
    }
  }