package test.cases.documentation

import org.scalatest.funsuite.AsyncFunSuite
import pipeline.producer.AMFProducer

class IsFullyDocumentedTest extends AsyncFunSuite {

  private def fullyDocumentedAssertion(apiPath: String)(expected: Boolean) = {
    val baseDir  = DocumentationCase.baseDir
    val producer = AMFProducer(s"file://$baseDir/$apiPath")
    producer
      .produce()
      .map(DocumentationCase.pipeline.run)
      .map(DocumentationCase.isFullyDocumented.consume)
      .map(actual => assert(actual == expected))
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
