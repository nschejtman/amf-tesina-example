package test.cases.documentation

import pipeline.Pipeline
import pipeline.producer.AMFProducer

class IsFullyDocumentedCaseStudyTest extends DocumentationCaseStudyTest {

  private def fullyDocumentedAssertion(apiPath: String)(expected: Boolean) = {
    val producer        = AMFProducer(s"file://$baseDir/$apiPath")
    val actual          = Pipeline(producer, transformations, isFullyDocumentedConsumer).run()
    assert(actual == expected)
  }

  private def assertFullyDocumented(apiPath: String) = fullyDocumentedAssertion(apiPath)(expected = true)

  private def assertNotFullyDocumented(apiPath: String) = fullyDocumentedAssertion(apiPath)(expected = false)

  test("Fully-undocumented case") {
    assertNotFullyDocumented("fully-undocumented/api.raml")
  }

  test("Fully-documented case") {
    assertFullyDocumented("fully-documented/api.raml")
  }

  test("Partially-documented case") {
    assertNotFullyDocumented("partially-documented/api.raml")
  }

}
