package test.cases.pii

import pipeline.Pipeline
import pipeline.producer.JenaProducer
import utils.jena.Lang

class SensitivePercentageCaseStudyTest extends PIICaseStudyTest {

  private def assertPercentageFrom(apiPath: String, lang: Lang)(expected: Float) = {
    val producer = JenaProducer(s"file://$baseDir/$apiPath", lang)
    val actual   = Pipeline(producer, transformations, APISensitivityPercentageConsumer).run()
    assert(actual == expected)
  }

  test("Test 0% percentage case") {
    assertPercentageFrom("non-sensitive-api/api.jsonld", Lang.JsonLd)(0.0f)
  }

  test("Test 47% percentage case") {
    assertPercentageFrom("sensitive-data-type-property/api.jsonld", Lang.JsonLd)(0.47f)
  }

}
