package test.unit.utils
import org.scalatest.funsuite.AnyFunSuite
import utils.Conversions._

class ConversionsTest extends AnyFunSuite {
  test("Remove protocol from relative path") {
    val actual   = "file://path/to/some/file.txt".withProtocol("")
    val expected = "path/to/some/file.txt"
    assert(actual == expected)
  }

  test("Remove protocol from absolute path") {
    val actual   = "file:///path/to/some/file.txt".withProtocol("")
    val expected = "/path/to/some/file.txt"
    assert(actual == expected)
  }

  test("Replace protocol from relative path") {
    val actual   = "file://path/to/some/file.txt".withProtocol("http://")
    val expected = "http://path/to/some/file.txt"
    assert(actual == expected)
  }

  test("Replace protocol from absolute path") {
    val actual   = "file:///path/to/some/file.txt".withProtocol("http://")
    val expected = "http:///path/to/some/file.txt"
    assert(actual == expected)
  }

  test("Remove extension") {
    val actual   = "file.txt".withExtension("")
    val expected = "file"
    assert(actual == expected)
  }

  test("Replace extension") {
    val actual   = "file.txt".withExtension(".md")
    val expected = "file.md"
    assert(actual == expected)
  }

}
