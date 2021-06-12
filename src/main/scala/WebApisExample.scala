import amf.client.model.document.BaseUnit
import amf.client.parse.Parser
import amf.client.resolve.Resolver
import amf.core.remote.{Raml10, Vendor}
import helpers.Conversions._
import helpers.{InitializationHelper, MediaType, Rdf}
import org.apache.jena.rdf.model.Model

import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.duration.Duration
import scala.concurrent.{Await, Future}
import scala.util.{Failure, Success}

//noinspection SameParameterValue
object WebApisExample {
  def main(args: Array[String]): Unit = {
    val result = for {
      _ <- InitializationHelper.init()
      _ <- run("file://src/main/resources/web-apis/raml/well-documented.raml", Raml10)
    } yield {
      true
    }

    result onComplete {
      case Success(_) => println("Finished with success")
      case Failure(f) => println(s"Finished with failure: ${f.toString}")
    }

    Await.ready(result, Duration.Inf)
  }

  private def parse(fileUrl: String, vendor: Vendor): Future[BaseUnit] = {
    println(s"Started: parse $fileUrl")
    val parser = new Parser(vendor.name, MediaType.forVendor(vendor))
    val result = parser.parseFileAsync(fileUrl).get()
    println(s"Done: parse $fileUrl")
    result.wrapFuture
  }

  private def resolve(baseUnit: BaseUnit, vendor: Vendor): Future[BaseUnit] = {
    println(s"Started: resolve ${baseUnit.id}")
    val resolver = new Resolver(vendor.name)
    val result   = resolver.resolve(baseUnit)
    println(s"Done: resolve ${baseUnit.id}")
    result.wrapFuture
  }

  private def run(fileUrl: String, vendor: Vendor) = {
    for {
      parsed         <- parse(fileUrl, vendor)
      resolved       <- resolve(parsed, vendor)
      rdf            <- Rdf.AMF.toRdfModel(resolved, helpers.Namespaces.ns)
      _              <- Rdf.IO.write(rdf, fileUrl.noProtocol.withExtension(".jsonld"), "JSON-LD", resolved.id)
      ontology       <- Rdf.IO.read("src/main/resources/web-apis/ontologies/Documentation.ontology.ttl", lang = "TTL")
      inferenceModel <- Rdf.Inference.default(ontology, rdf)
      _              <- runQueriesOn(inferenceModel)
    } yield {
      rdf
    }
  }

  private def runQueriesOn(model: Model): Future[Unit] = {
    val rootDir = "src/main/resources/web-apis/queries"
    for {
      isWellDocumented <- Rdf.Query.ask(model, s"$rootDir/is-fully-documented.sparql")
      _                <- println(s"Fully documented: $isWellDocumented").wrapFuture
      coverageResult   <- Rdf.Query.select(model, s"$rootDir/documentation-coverage.sparql")
      _ <- Future.successful {
        val solution        = coverageResult.next()
        val total           = solution.getLiteral("total").getDouble
        val totalDocumented = solution.getLiteral("totalDocumented").getDouble
        val coverage        = totalDocumented / total
        println(s"Documentation coverage: ${coverage.asPercentage}")
      }
      _         <- Rdf.IO.print(coverageResult)
      _         <- println("Missing description for: ").wrapFuture
      resultSet <- Rdf.Query.select(model, s"$rootDir/list-undocumented.sparql")
      _         <- Rdf.IO.print(resultSet)
    } yield {
      Unit
    }
  }

}
