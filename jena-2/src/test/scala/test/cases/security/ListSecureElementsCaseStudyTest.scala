package test.cases.security

import com.hp.hpl.jena.query.ResultSet
import org.scalatest.Assertion
import pipeline.Pipeline
import pipeline.producer.JenaProducer
import utils.jena.Lang

class ListSecureElementsCaseStudyTest extends SecurityCaseStudyTest {

  private def listSensitiveFrom(apiPath: String, lang: Lang)(assertionFn: ResultSet => Assertion) = {
    val producer = JenaProducer(s"file://$baseDir/$apiPath", lang)
    val actual   = Pipeline(producer, transformations, listSecureElementsConsumer).run()
    assertionFn(actual)
  }

  test("List secure elements from bottom-up-semi-transfer") {
    listSensitiveFrom("bottom-up-semi-transfer/api.raml.jsonld", Lang.JsonLd) { rs =>
      var actual: Set[String] = Set()
      rs.forEachRemaining(actual += _.get("id").toString) // collect

      val base = "file://jena-2/src/test/resources/test/cases/security/bottom-up-semi-transfer/api.raml#"
      val expected = Set(
          s"$base/web-api/end-points/%2FendpointA/post",
          s"$base/web-api/end-points/%2FendpointA/get",
          s"$base/web-api/end-points/%2FendpointA"
      )
      assert(actual == expected)
    }
  }

  test("List secure elements from bottom-up-transfer") {
    listSensitiveFrom("bottom-up-transfer/api.raml.jsonld", Lang.JsonLd) { rs =>
      var actual: Set[String] = Set()
      rs.forEachRemaining(actual += _.get("id").toString) // collect

      val base = "file://jena-2/src/test/resources/test/cases/security/bottom-up-transfer/api.raml#"
      val expected = Set(
          s"$base/web-api/end-points/%2FendpointA/post",
          s"$base/web-api/end-points/%2FendpointA/get",
          s"$base/web-api/end-points/%2FendpointA",
          s"$base/web-api/end-points/%2FendpointB/post",
          s"$base/web-api/end-points/%2FendpointB/get",
          s"$base/web-api/end-points/%2FendpointB",
          s"$base/web-api"
      )
      assert(actual == expected)
    }
  }
}
