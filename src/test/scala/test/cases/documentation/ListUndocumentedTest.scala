package test.cases.documentation
import org.apache.jena.query.ResultSet
import org.scalatest.Assertion
import org.scalatest.funsuite.AsyncFunSuite
import pipeline.producer.AMFProducer

class ListUndocumentedTest extends AsyncFunSuite {

  private def listUndocumentedFrom(apiPath: String)(assertion: ResultSet => Assertion) = {
    val baseDir  = DocumentationCase.baseDir
    val producer = AMFProducer(s"file://$baseDir/$apiPath")
    producer
      .produce()
      .map(DocumentationCase.pipeline.run)
      .map(DocumentationCase.listUndocumented.consume)
      .map(assertion)
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
      val base     = "file://src/test/resources/test/cases/documentation/partially-documented/api.raml#"
      val expected = Set(s"$base/web-api", s"$base/web-api/end-points/%2Fagents/get/200/application%2Fjson/array/schema")

      val node0 = undocumentedRS.next()
      val node1 = undocumentedRS.next()

      val actual = Set(node0.get("id").toString, node1.get("id").toString)

      assert(actual == expected)
    }
  }

}
