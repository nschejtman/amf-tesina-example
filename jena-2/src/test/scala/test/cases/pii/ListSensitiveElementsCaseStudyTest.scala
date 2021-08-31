package test.cases.pii
import com.hp.hpl.jena.query.ResultSet
import org.scalatest.Assertion
import pipeline.Pipeline
import pipeline.producer.JenaProducer
import utils.jena.Lang

class ListSensitiveElementsCaseStudyTest extends PIICaseStudyTest {

  private def listSensitiveFrom(apiUrl: String, lang: Lang)(assertionFn: ResultSet => Assertion) = {
    val producer = JenaProducer(s"file://$baseDir/$apiUrl", lang)
    val actual   = Pipeline(producer, transformations, listSensitiveElementsConsumer).run()
    assertionFn(actual)
  }

  test("List sensitive from sensitive-data-type-property") {
    listSensitiveFrom("sensitive-data-type-property/api.jsonld", Lang.JsonLd) { rs =>
      var actual: Set[String] = Set()
      rs.forEachRemaining(actual += _.get("id").toString) // collect

      val base = "file://src/test/resources/test/cases/pii/sensitive-data-type-property/api.raml"

      val expected = Set(
          s"$base#/declarations/types/User",
          s"$base#/declarations/types/User/property/fullName",
          s"$base#/declarations/types/User/property/fullName/scalar/fullName",
          s"$base#/web-api",
          s"$base#/web-api/end-points//users",
          s"$base#/web-api/end-points//users/get",
          s"$base#/web-api/end-points//users/get/200",
          s"$base#/web-api/end-points//users/get/200/application/json",
          s"$base#/web-api/end-points//users/get/200/application/json/array/schema",
      )

      assert(actual == expected)
    }
  }

  test("List sensitive from non-sensitive-api") {
    listSensitiveFrom("non-sensitive-api/api.jsonld", Lang.JsonLd) { rs =>
      var actual: Set[String] = Set()
      rs.forEachRemaining(actual += _.get("id").toString) // collect
      assert(actual.isEmpty)
    }
  }
}
