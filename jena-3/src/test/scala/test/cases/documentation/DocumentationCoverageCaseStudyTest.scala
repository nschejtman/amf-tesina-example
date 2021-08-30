package test.cases.documentation
import pipeline.Pipeline
import pipeline.producer.AMFProducer

class DocumentationCoverageCaseStudyTest extends DocumentationCaseStudyTest {

  private def assertCoverageFrom(apiPath: String)(expected: Float) = {
    val producer = AMFProducer(s"file://$baseDir/$apiPath")
    val actual   = Pipeline(producer, transformations, coverageConsumer).run()
    assert(actual == expected)
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
