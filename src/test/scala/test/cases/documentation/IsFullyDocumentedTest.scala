package test.cases.documentation

import amf.core.remote.{Raml10, Vendor}
import org.apache.jena.query.ResultSet
import org.scalatest.Assertion
import org.scalatest.funsuite.AsyncFunSuite
import pipeline.producer.AMFProducer

class IsFullyDocumentedTest extends AsyncFunSuite {

  private def fullyDocumentedAssertion(apiPath: String, vendor: Vendor)(expected: Boolean) = {
    val baseDir  = DocumentationCase.baseDir
    val producer = AMFProducer(s"file://$baseDir/$apiPath", vendor)
    producer
      .produce()
      .map(DocumentationCase.pipeline.run)
      .map(DocumentationCase.isFullyDocumented.consume)
      .map(actual => assert(actual == expected))
  }

  private def assertFullyDocumented(apiPath: String, vendor: Vendor) = fullyDocumentedAssertion(apiPath, vendor)(expected = true)

  private def assertNotFullyDocumented(apiPath: String, vendor: Vendor) = fullyDocumentedAssertion(apiPath, vendor)(expected = false)

  test("Fully-undocumented case") {
    assertNotFullyDocumented("fully-undocumented/api.raml", Raml10)
  }

  test("Fully-documented case") {
    assertFullyDocumented("fully-documented/api.raml", Raml10)
  }

  test("Partially-documented case") {
    assertNotFullyDocumented("partially-documented/api.raml", Raml10)
  }

}
