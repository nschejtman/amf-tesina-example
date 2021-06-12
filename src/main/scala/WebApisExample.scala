import amf.client.model.document.BaseUnit
import amf.client.parse.Parser
import amf.client.resolve.Resolver
import amf.core.remote.{Oas20, Raml10, Vendor}
import helpers.{InitializationHelper, MediaType, RdfHelper}
import org.apache.jena.query.{QueryExecutionFactory, QueryFactory}
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
      _ <- cycle("file://src/main/resources/web-apis/raml/well-documented.raml", Raml10)
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
    val result = Future.successful {
      val parser = new Parser(vendor.name, MediaType.forVendor(vendor))
      parser.parseFileAsync(fileUrl).get()
    }
    println(s"Done: parse $fileUrl")
    result
  }

  private def resolve(baseUnit: BaseUnit, vendor: Vendor): Future[BaseUnit] = {
    println(s"Started: resolve ${baseUnit.id}")
    val result = Future.successful {
      val resolver = new Resolver(vendor.name)
      resolver.resolve(baseUnit)
    }
    println(s"Done: resolve ${baseUnit.id}")
    result
  }

  private def query(model: Model, queryUrl: String) = {
    val query     = QueryFactory.read(queryUrl)
    val execution = QueryExecutionFactory.create(query, model)
    execution.execConstruct().write(System.out, "JSON-LD")
    Future.unit
  }

  private def cycle(fileUrl: String, vendor: Vendor) = {
    for {
      parsed   <- parse(fileUrl, vendor)
      resolved <- resolve(parsed, vendor)
      rdf      <- RdfHelper.toRdfModel(resolved, helpers.Namespaces.ns)
      _ <- Future.successful {
        val targetName = fileUrl.stripPrefix("file://").replaceFirst("\\.(raml|json)", ".jsonld")
        RdfHelper.write(rdf, targetName, "JSON-LD", resolved.id)
      }
      _ <- query(rdf, "src/main/resources/web-apis/well-documented.sparql")
    } yield {
      rdf
    }
  }

}
