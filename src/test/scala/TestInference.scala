import helpers.Rdf
import org.scalatest.funsuite.AsyncFunSuite

import scala.concurrent.ExecutionContext

class TestInference() extends AsyncFunSuite with HasLogger {
  protected implicit val ec: ExecutionContext = scala.concurrent.ExecutionContext.Implicits.global

  test("Test subClassOf inference with Pellet") {
    for {
      data     <- Rdf.IO.read("src/test/resources/basic/data.ttl", "TTL")
      schema   <- Rdf.IO.read("src/test/resources/basic/ontology.ttl", "TTL")
      infModel <- Rdf.Inference.default(data, schema)
      result   <- Rdf.Query.ask(infModel, "src/test/resources/basic/test.sparql")
    } yield {
      assert(result)
    }
  }
}
