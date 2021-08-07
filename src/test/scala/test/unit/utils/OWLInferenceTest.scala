package test.unit.utils

import org.scalatest.funsuite.AnyFunSuite
import utils.jena.Lang.Ttl
import utils.jena.{IO, InferenceModel, Query, Reasoners}
import utils.logger.WithDefaultLogger

import scala.concurrent.ExecutionContext

class OWLInferenceTest() extends AnyFunSuite with WithDefaultLogger {
  protected implicit val ec: ExecutionContext = scala.concurrent.ExecutionContext.Implicits.global
  val baseDir                                 = "src/test/resources/test/unit/utils/owl-inference-test"

  test("Test subClassOf inference with default reasoner") {
    val data     = IO.read(s"$baseDir/subClassOf/data.ttl", Ttl)
    val schema   = IO.read(s"$baseDir/subClassOf/ontology.ttl", Ttl)
    val infModel = InferenceModel.from(data, schema, Reasoners.default())
    val result   = Query.ask(infModel, s"$baseDir/subClassOf/test.sparql")
    assert(result)
  }

  test("Test subPropertyOf inference with default reasoner") {
    val data     = IO.read(s"$baseDir/subPropertyOf/data.ttl", Ttl)
    val schema   = IO.read(s"$baseDir/subPropertyOf/ontology.ttl", Ttl)
    val infModel = InferenceModel.from(data, schema, Reasoners.default())
    val result   = Query.ask(infModel, s"$baseDir/subPropertyOf/test.sparql")
    assert(result)
  }

  test("Test subClassOf inference with Pellet reasoner") {
    val data     = IO.read(s"$baseDir/subClassOf/data.ttl", Ttl)
    val schema   = IO.read(s"$baseDir/subClassOf/ontology.ttl", Ttl)
    val infModel = InferenceModel.from(data, schema, Reasoners.pellet())
    val result   = Query.ask(infModel, s"$baseDir/subClassOf/test.sparql")
    assert(result)
  }

  test("Test subPropertyOf inference with Pellet reasoner") {
    val data     = IO.read(s"$baseDir/subPropertyOf/data.ttl", Ttl)
    val schema   = IO.read(s"$baseDir/subPropertyOf/ontology.ttl", Ttl)
    val infModel = InferenceModel.from(data, schema, Reasoners.pellet())
    val result   = Query.ask(infModel, s"$baseDir/subPropertyOf/test.sparql")
    assert(result)
  }

}
