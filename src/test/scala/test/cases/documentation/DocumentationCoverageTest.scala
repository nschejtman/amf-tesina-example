package test.cases.documentation
import org.scalatest.funsuite.AsyncFunSuite
import pipeline.producer.AMFProducer

class DocumentationCoverageTest extends AsyncFunSuite {

  private def assertCoverageFrom(apiPath: String)(expected: Float) = {
    val baseDir          = DocumentationCase.baseDir
    val producer         = AMFProducer(s"file://$baseDir/$apiPath")
    val pipeline         = DocumentationCase.pipeline
    val coverageConsumer = DocumentationCase.coverage

    producer
      .produce()
      .map(pipeline.run)
      .map(coverageConsumer.consume)
      .map { actual =>
        assert(actual == expected)
      }
  }

  test("Test 0% coverage case") {
    assertCoverageFrom("fully-undocumented/api.raml")(0.0f)
  }

  test("Test 75% coverage case") {
    assertCoverageFrom("partially-documented/api.raml")(0.75f)
  }

  test("Test 100% coverage case") {
    assertCoverageFrom("fully-documented/api.raml")(1.0f)
  }

}
