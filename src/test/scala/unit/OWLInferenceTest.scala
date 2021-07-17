package unit

import utils.jena.Lang.Ttl
import utils.jena.{IO, InferenceModel, Query, Reasoners}
import org.scalatest.funsuite.AnyFunSuite
import utils.logger.WithDefaultLogger

import scala.concurrent.ExecutionContext

class BasicInferenceTest() extends AnyFunSuite with WithDefaultLogger {
  protected implicit val ec: ExecutionContext = scala.concurrent.ExecutionContext.Implicits.global

  test("Test subClassOf inference with Pellet") {
    val data     = IO.read("src/test/resources/basic/data.ttl", Ttl)
    val schema   = IO.read("src/test/resources/basic/ontology.ttl", Ttl)
    val infModel = InferenceModel.from(data, schema, Reasoners.default())
    val result   = Query.ask(infModel, "src/test/resources/basic/test.sparql")
    assert(result)
  }
}
