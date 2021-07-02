import amf.core.remote.Vendor
import com.typesafe.scalalogging.Logger
import helpers.Conversions.Url
import helpers.{Amf, InitializationHelper, Rdf}
import org.apache.jena.rdf.model.Model

import scala.concurrent.duration.Duration
import scala.concurrent.{Await, ExecutionContext, Future}
import scala.util.{Failure, Success}

//noinspection SameParameterValue
trait Example {
  protected implicit val logger: Logger       = Logger[this.type]
  protected implicit val ec: ExecutionContext = scala.concurrent.ExecutionContext.Implicits.global

  protected val resources  = "file://src/main/resources"
  protected val raml       = s"$resources/web-apis/raml"
  protected val ontologies = s"$resources/web-apis/ontologies"
  protected val queries    = s"$resources/web-apis/queries"

  protected def obtainModelFromAmf(fileUrl: String, vendor: Vendor): Future[Model] = {
    for {
      parsed   <- Amf.parse(fileUrl, vendor)
      resolved <- Amf.resolve(parsed, vendor)
      _        <- Amf.render(resolved, fileUrl.withExtension(".jsonld"))
      rdf      <- Rdf.IO.read(fileUrl.noProtocol.withExtension(".jsonld"))
    } yield {
      rdf
    }
  }

  protected def obtainInferenceModelFrom(model: Model, ontologyUrl: String, fileUrl: String): Future[Model] = {
    for {
      ontology       <- Rdf.IO.read(ontologyUrl.noProtocol, lang = "TTL")
      inferenceModel <- Rdf.Inference.default(ontology, model)
      _              <- Rdf.IO.write(inferenceModel, fileUrl.noProtocol.withExtension(".inference.jsonld"), "JSON-LD", fileUrl)
    } yield {
      inferenceModel
    }
  }

  protected def runQueriesOn(model: Model, fileUrl: String): Future[Unit]

  protected def run(fileUrl: String, ontologyUrl: String, vendor: Vendor): Future[Unit] = {
    for {
      model          <- obtainModelFromAmf(fileUrl, vendor)          // generate basic graph
      inferenceModel <- obtainInferenceModelFrom(model, ontologyUrl, fileUrl) // enrich graph
      _              <- runQueriesOn(inferenceModel, fileUrl)        // query graph
    } yield {
      println()
    }
  }

  protected def kernel(): Future[Unit]

  protected def main(args: Array[String]): Unit = {
    val result = for {
      _ <- InitializationHelper.init()
      _ <- kernel()
    } yield {
      println()
    }

    result onComplete {
      case Success(_) => logger.info("Finished with success")
      case Failure(f) => logger.error(s"Finished with failure: ${f.toString}")
    }

    Await.ready(result, Duration.Inf)
  }

}
