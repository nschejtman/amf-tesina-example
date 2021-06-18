import amf.core.remote.{Raml10, Vendor}
import com.typesafe.scalalogging.Logger
import helpers.Conversions._
import helpers.{Amf, InitializationHelper, Rdf}
import org.apache.jena.rdf.model.Model

import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.duration.Duration
import scala.concurrent.{Await, Future}
import scala.util.{Failure, Success}

//noinspection SameParameterValue
object WebApiDocumentationExample {
  implicit val logger: Logger = Logger[this.type]

  def main(args: Array[String]): Unit = {
    val result = for {
      _ <- InitializationHelper.init()
      _ <- run("file://src/main/resources/web-apis/raml/fully-documented.raml", Raml10)
      _ <- run("file://src/main/resources/web-apis/raml/partially-documented.raml", Raml10)
    } yield {
      println()
    }

    result onComplete {
      case Success(_) => logger.info("Finished with success")
      case Failure(f) => logger.error(s"Finished with failure: ${f.toString}")
    }

    Await.ready(result, Duration.Inf)
  }

  private def run(fileUrl: String, vendor: Vendor) = {
    for {
      parsed         <- Amf.parse(fileUrl, vendor)
      resolved       <- Amf.resolve(parsed, vendor)
      _              <- Amf.render(resolved, fileUrl.withExtension(".jsonld"))
      rdf            <- Rdf.IO.read(fileUrl.noProtocol.withExtension(".jsonld"))
      ontology       <- Rdf.IO.read("src/main/resources/web-apis/ontologies/Documentation.ontology.ttl", lang = "TTL")
      inferenceModel <- Rdf.Inference.default(ontology, rdf)
      _              <- Rdf.IO.write(inferenceModel, fileUrl.noProtocol.withExtension(".inf.jsonld"), "JSON-LD", resolved.id)
      _              <- runQueriesOn(inferenceModel, fileUrl)
    } yield {
      rdf
    }
  }

  private def runQueriesOn(model: Model, modelName: String): Future[Unit] = {
    println(Console.BLUE)
    println(s"Querying model: $modelName")
    println(Console.RESET)
    val queriesDir = "src/main/resources/web-apis/queries"
    for {
      isFullyDocumented <- queryIsFullyDocumented(model, queriesDir)
      _                 <- queryCoverage(model, queriesDir)
      _                 <- if (!isFullyDocumented) queryUndocumentedNodes(model, queriesDir) else Future.unit
    } yield {
      Unit
    }
  }

  private def queryIsFullyDocumented(model: Model, queriesDir: String) = {
    for {
      isFullyDocumented <- Rdf.Query.ask(model, s"$queriesDir/is-fully-documented.sparql")
    } yield {
      println(s"Fully documented: $isFullyDocumented")
      isFullyDocumented
    }
  }
  private def queryCoverage(model: Model, queriesDir: String) = {
    for {
      coverageResult <- Rdf.Query.select(model, s"$queriesDir/documentation-coverage.sparql")
    } yield {
      val solution        = coverageResult.next()
      val total           = solution.getLiteral("total").getDouble
      val totalDocumented = solution.getLiteral("totalDocumented").getDouble
      val coverage        = totalDocumented / total
      println(s"Documentation coverage: ${coverage.asPercentage}")
    }
  }
  private def queryUndocumentedNodes(model: Model, queriesDir: String) = {
    for {
      _         <- println("Missing description for: ").wrapFuture
      resultSet <- Rdf.Query.select(model, s"$queriesDir/list-undocumented.sparql")
      _         <- Rdf.IO.print(resultSet)
    } yield {
      Unit
    }
  }
}
