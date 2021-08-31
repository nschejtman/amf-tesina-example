package test.cases.security

import pipeline.Pipeline
import pipeline.producer.JenaProducer
import utils.jena.Lang

class SecurityCoverageCaseStudyTest extends SecurityCaseStudyTest {

  private def assertCoverageFrom(apiPath: String, lang: Lang)(expected: Float) = {
    val producer = JenaProducer(s"file://$baseDir/$apiPath", lang)
    val actual   = Pipeline(producer, transformations, APISecurityCoverageConsumer).run()
    assert(actual == expected)
  }

  test("Test 100% coverage case") {
    assertCoverageFrom("bottom-up-transfer/api.raml.jsonld", Lang.JsonLd)(1.0f)
  }

  test("Test 43% coverage case") {
    assertCoverageFrom("bottom-up-semi-transfer/api.raml.jsonld", Lang.JsonLd)(0.43f)
  }

}
