package test.cases.documentation
import amf.core.remote.{Raml10, Vendor}
import org.scalatest.funsuite.AsyncFunSuite
import pipeline.producer.AMFProducer

class DocumentationCoverageTest extends AsyncFunSuite {

  private def assertCoverageFrom(apiPath: String, vendor: Vendor)(expected: Float) = {
    val baseDir          = DocumentationCase.baseDir
    val producer         = AMFProducer(s"file://$baseDir/$apiPath", vendor)
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
    assertCoverageFrom("fully-undocumented/api.raml", Raml10)(0.0f)
  }

  test("Test 75% coverage case") {
    assertCoverageFrom("partially-documented/api.raml", Raml10)(0.75f)
  }

  test("Test 100% coverage case") {
    assertCoverageFrom("fully-documented/api.raml", Raml10)(1.0f)
  }

}
