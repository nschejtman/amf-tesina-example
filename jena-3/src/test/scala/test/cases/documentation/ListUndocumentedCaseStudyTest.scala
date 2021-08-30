package test.cases.documentation
import org.apache.jena.query.ResultSet
import org.scalatest.Assertion
import pipeline.Pipeline
import pipeline.producer.AMFProducer

class ListUndocumentedCaseStudyTest extends DocumentationCaseStudyTest {

  private def listUndocumentedFrom(apiPath: String)(assertionFn: ResultSet => Assertion) = {
    val producer        = AMFProducer(s"file://$baseDir/$apiPath")
    val actual          = Pipeline(producer, transformations, listUndocumentedConsumer).run()
    assertionFn(actual)
  }

  test("List undocumented nodes in fully-undocumented") {
    listUndocumentedFrom("fully-undocumented/api.raml") { undocumentedRS =>
      var count = 0
      undocumentedRS.forEachRemaining(_ => count += 1)
      assert(count == 8)
    }
  }

  test("List undocumented nodes in fully-documented") {
    listUndocumentedFrom("fully-documented/api.raml") { undocumentedRS =>
      assert(!undocumentedRS.hasNext)
    }
  }

  test("List undocumented nodes in partially-documented") {
    listUndocumentedFrom("partially-documented/api.raml") { undocumentedRS =>
      val base     = "file://jena-3/src/test/resources/test/cases/documentation/partially-documented/api.raml#"
      val expected = Set(s"$base/web-api", s"$base/web-api/end-points/%2Fagents/get/200/application%2Fjson/array/schema")

      val node0 = undocumentedRS.next()
      val node1 = undocumentedRS.next()

      val actual = Set(node0.get("id").toString, node1.get("id").toString)

      assert(actual == expected)
    }
  }

}
