import amf.core.remote.Raml10
import helpers.Conversions.{Fut, Url}
import helpers.Rdf
import org.apache.jena.rdf.model.{InfModel, Model}

import scala.concurrent.Future
object SecurityExample extends Example {

  override protected val inferenceProvider: (Model, Model) => Future[InfModel] = Rdf.Inference.pellet

  def main(args: Array[String]): Unit                                          = mainImpl()

  override protected def runQueriesOn(model: Model, fileUrl: String): Future[Unit] = {
    println(Console.BLUE)
    println(s"Querying model: $fileUrl")
    println(Console.RESET)
    for {
//      _ <- queryInsecureElements(model)
      _ <- querySecureElements(model)
    } yield {
      Unit
    }
  }

  private def queryInsecureElements(model: Model) = {
    for {
      _         <- println("The following elements are not secure: ").wrapFuture
      resultSet <- Rdf.Query.select(model, s"$queries/list-insecure.sparql".noProtocol)
      _         <- Rdf.IO.print(resultSet)
    } yield {
      Unit
    }
  }

  private def querySecureElements(model: Model) = {
    for {
      _         <- println("The following elements are secure: ").wrapFuture
      resultSet <- Rdf.Query.select(model, s"$queries/list-secure.sparql".noProtocol)
      _         <- Rdf.IO.print(resultSet)
    } yield {
      Unit
    }
  }

  override protected def kernel(): Future[Unit] = {
    for {
      _ <- run(s"$raml/insecure/api.raml", s"$ontologies/Security.ontology.ttl", Raml10)
      _ <- run(s"$raml/secure/api.raml", s"$ontologies/Security.ontology.ttl", Raml10)
    } yield {
      Unit
    }
  }
}
