package test.cases.security

import pipeline.Pipeline
import pipeline.producer.JenaProducer
import utils.jena.Lang

//noinspection SameParameterValue
class IsSecureCaseStudyTest extends SecurityCaseStudyTest {

  private def fullyDocumentedAssertion(apiPath: String, lang: Lang)(expected: Boolean) = {
    val producer = JenaProducer(s"file://$baseDir/$apiPath", lang)
    val actual   = Pipeline(producer, transformations, isSecureAPIConsumer).run()
    assert(actual == expected)
  }

  private def assertSecure(apiPath: String, lang: Lang) = fullyDocumentedAssertion(apiPath, lang)(expected = true)

  private def assertInsecure(apiPath: String, lang: Lang) = fullyDocumentedAssertion(apiPath, lang)(expected = false)

  test("Secure case") {
    assertSecure("bottom-up-transfer/api.raml.jsonld", Lang.JsonLd)
  }

  test("Insecure case") {
    assertInsecure("bottom-up-semi-transfer/api.raml.jsonld", Lang.JsonLd)
  }

}
