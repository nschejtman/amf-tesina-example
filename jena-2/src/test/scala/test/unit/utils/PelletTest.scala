package test.unit.utils
import com.hp.hpl.jena.ontology.OntModelSpec
import com.hp.hpl.jena.query.{QueryExecutionFactory, QueryFactory}
import com.hp.hpl.jena.rdf.model.ModelFactory
import org.mindswap.pellet.jena.PelletReasonerFactory
import org.scalatest.funsuite.AnyFunSuite

import java.io.FileInputStream

class PelletTest extends AnyFunSuite {

  test("PII Case Study simple example") {
    // Read data
    val data            = ModelFactory.createDefaultModel()
    val dataInputStream = new FileInputStream("jena-2/src/test/resources/test/cases/pii/simple-transfer/simple.ttl")
    data.read(dataInputStream, null, "TTL")

    // Read ontology
    val ontology            = ModelFactory.createOntologyModel()
    val ontologyInputStream = new FileInputStream("jena-2/src/test/resources/test/cases/pii/PII.ontology.ttl")
    ontology.read(ontologyInputStream, null, "TTL")

    // Create inference model using Pellet
    val reasoner = PelletReasonerFactory.theInstance().create()
    val infModel = ModelFactory.createInfModel(reasoner, data.union(ontology))

    // Query inference model
    val query          = QueryFactory.read("jena-2/src/test/resources/test/cases/pii/list-pii-sensitive.sparql")
    val queryExecution = QueryExecutionFactory.create(query, infModel)
    val resultSet      = queryExecution.execSelect()

    // Collect query results
    val expected            = Set("http://test/simple-transfer#A", "http://test/simple-transfer#B", "http://test/simple-transfer#C")
    var actual: Set[String] = Set()
    resultSet.forEachRemaining(actual += _.get("id").toString)

    assert(actual == expected)
  }

  test("PII Case Study simple example - Alternative implementation") {
    val ontology            = ModelFactory.createOntologyModel(OntModelSpec.OWL_MEM)
    val ontologyInputStream = new FileInputStream("jena-2/src/test/resources/test/cases/pii/PII.ontology.ttl")
    ontology.read(ontologyInputStream, null, "TTL")
    val dataInputStream = new FileInputStream("jena-2/src/test/resources/test/cases/pii/simple-transfer/simple.ttl")
    ontology.read(dataInputStream, null, "TTL")

    val ontModel = ModelFactory.createOntologyModel(PelletReasonerFactory.THE_SPEC, ontology)

    // Query inference model
    val query          = QueryFactory.read("jena-2/src/test/resources/test/cases/pii/list-pii-sensitive.sparql")
    val queryExecution = QueryExecutionFactory.create(query, ontModel)
    val resultSet      = queryExecution.execSelect()

    // Collect query results
    val expected            = Set("http://test/simple-transfer#A", "http://test/simple-transfer#B", "http://test/simple-transfer#C")
    var actual: Set[String] = Set()
    resultSet.forEachRemaining(actual += _.get("id").toString)

    assert(actual == expected)
  }

}
