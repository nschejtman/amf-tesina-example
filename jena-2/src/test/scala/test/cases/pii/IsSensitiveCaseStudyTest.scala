package test.cases.pii

import pipeline.Pipeline
import pipeline.producer.JenaProducer
import utils.jena.Lang

//noinspection SameParameterValue
class IsSensitiveCaseStudyTest extends PIICaseStudyTest {

  private def fullyDocumentedAssertion(apiPath: String, lang: Lang)(expected: Boolean) = {
    val producer = JenaProducer(s"file://$baseDir/$apiPath", lang)
    val actual   = Pipeline(producer, transformations, isSensitiveAPIConsumer).run()
    assert(actual == expected)
  }

  private def assertSensitive(apiPath: String, lang: Lang) = fullyDocumentedAssertion(apiPath, lang)(expected = true)

  private def assertNotSensitive(apiPath: String, lang: Lang) = fullyDocumentedAssertion(apiPath, lang)(expected = false)

  test("Fully-undocumented case") {
    assertNotSensitive("non-sensitive-api/api.jsonld", Lang.JsonLd)
  }

  test("Fully-documented case") {
    assertSensitive("sensitive-data-type-property/api.jsonld", Lang.JsonLd)
  }

}
