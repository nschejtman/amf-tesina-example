import amf.core.remote.Raml10
import helpers.Conversions._
import helpers.Rdf
import org.apache.jena.rdf.model.Model

import scala.concurrent.Future

//noinspection SameParameterValue
object WebApiDocumentationExample extends Example {

  override def runQueriesOn(model: Model, modelName: String): Future[Unit] = {
    println(Console.BLUE)
    println(s"Querying model: $modelName")
    println(Console.RESET)
    for {
      isFullyDocumented <- queryIsFullyDocumented(model)
      _                 <- queryCoverage(model)
      _                 <- if (!isFullyDocumented) queryUndocumentedNodes(model) else Future.unit
    } yield {
      Unit
    }
  }

  private def queryIsFullyDocumented(model: Model) = {
    for {
      isFullyDocumented <- Rdf.Query.ask(model, s"$queries/is-fully-documented.sparql".noProtocol)
    } yield {
      println(s"Fully documented: $isFullyDocumented")
      isFullyDocumented
    }
  }
  private def queryCoverage(model: Model) = {
    for {
      coverageResult <- Rdf.Query.select(model, s"$queries/documentation-coverage.sparql".noProtocol)
    } yield {
      val solution        = coverageResult.next()
      val total           = solution.getLiteral("total").getDouble
      val totalDocumented = solution.getLiteral("totalDocumented").getDouble
      val coverage        = totalDocumented / total
      println(s"Documentation coverage: ${coverage.asPercentage}")
    }
  }
  private def queryUndocumentedNodes(model: Model) = {
    for {
      _         <- println("Missing description for: ").wrapFuture
      resultSet <- Rdf.Query.select(model, s"$queries/list-undocumented.sparql".noProtocol)
      _         <- Rdf.IO.print(resultSet)
    } yield {
      Unit
    }
  }
  override protected def kernel(): Future[Unit] = {
    for {
      _ <- run(s"$raml/fully-documented/api.raml", s"$ontologies/Documentation.ontology.ttl", Raml10)
      _ <- run(s"$raml/partially-documented/api.raml", s"$ontologies/Documentation.ontology.ttl", Raml10)
    } yield {
      Unit
    }
  }
}
