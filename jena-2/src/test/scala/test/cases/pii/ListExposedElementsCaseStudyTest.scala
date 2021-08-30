package test.cases.pii
import com.hp.hpl.jena.query.ResultSet
import org.scalatest.Assertion
import pipeline.Pipeline
import pipeline.producer.JenaProducer
import utils.jena.Lang

class ListExposedElementsCaseStudyTest extends PIICaseStudyTest {

  private def listSensitiveFrom(apiUrl: String, lang: Lang)(assertionFn: ResultSet => Assertion) = {
    val producer = JenaProducer(s"file://$baseDir/$apiUrl", lang)
    val actual   = Pipeline(producer, transformations, listSensitiveElementsConsumer).run()
    assertionFn(actual)
  }

  // Flaky
  ignore("List sensitive from sensitive property") {
    listSensitiveFrom("sensitive-data-type-property/api.jsonld", Lang.JsonLd) { rs =>
      var actual: Set[String] = Set()
      rs.forEachRemaining(actual += _.get("id").toString) // collect

      val base = "file://jena-2/src/test/resources/test/cases/pii/sensitive-data-type-property/api.raml"

      val expected = Set(
          s"$base",
          s"$base#/declarations/types/User",
          s"$base#/declarations/types/User/property/fullName",
          s"$base#/web-api",
          s"$base#/web-api/end-points/%2Fusers",
          s"$base#/web-api/end-points/%2Fusers/get",
          s"$base#/web-api/end-points/%2Fusers/get/200",
          s"$base#/web-api/end-points/%2Fusers/get/200/application%2Fjson",
          s"$base#/web-api/end-points/%2Fusers/get/200/application%2Fjson/array/schema",
      )

      assert(actual.intersect(expected) == expected) // difficult to explain all inferences
    }
  }

  // Flaky
  ignore("List sensitive from reduced case") {
    listSensitiveFrom("/simple-transfer/simple.ttl", Lang.Ttl) { rs =>
      var actual: Set[String] = Set()
      rs.forEachRemaining(actual += _.get("id").toString) // collect
      val expected = Set("http://test/simple-transfer#A", "http://test/simple-transfer#B", "http://test/simple-transfer#C")
      assert(actual == expected)
    }
  }
}
