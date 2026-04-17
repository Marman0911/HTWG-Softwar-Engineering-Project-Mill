import org.scalatest.flatspec.AnyFlatSpec
import org.scalatest.matchers.should.Matchers

class MySuite extends AnyFlatSpec with Matchers {
  "An example test" should "succeed" in {
    val obtained = 42
    val expected = 42
    obtained should be(expected)
  }
}
