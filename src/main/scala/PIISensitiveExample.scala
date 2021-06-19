import amf.core.remote.{Raml10, Vendor}
import com.typesafe.scalalogging.Logger
import helpers.Conversions._
import helpers.{Amf, InitializationHelper, Rdf}
import org.apache.jena.rdf.model.Model

import scala.concurrent.duration.Duration
import scala.concurrent.{Await, ExecutionContext, Future}
import scala.util.{Failure, Success}

//noinspection SameParameterValue
object PIISensitiveExample extends Pipeline {
  implicit val logger: Logger       = Logger[this.type]
  implicit val ec: ExecutionContext = scala.concurrent.ExecutionContext.Implicits.global

  def main(args: Array[String]): Unit = {
    val result = for {
      _ <- InitializationHelper.init()
      _ <- run(s"$raml/pii-sensitive/api.raml", s"$ontologies/PII.ontology.ttl", Raml10)
    } yield {
      println()
    }

    result onComplete {
      case Success(_) => logger.info("Finished with success")
      case Failure(f) => logger.error(s"Finished with failure: ${f.toString}")
    }

    Await.ready(result, Duration.Inf)
  }

  override protected def obtainModelFromAmf(fileUrl: String, vendor: Vendor): Future[Model] = {
    Rdf.IO.read(fileUrl.noProtocol.withExtension(".jsonld"))
  }

  override def runQueriesOn(model: Model, fileUrl: String): Future[Unit] = {
    println(Console.BLUE)
    println(s"Querying model: $fileUrl")
    println(Console.RESET)
    for {
      isPiiSensitive <- queryIsPIISensitive(model)
      _              <- queryExposure(model)
      _              <- if (isPiiSensitive) querySensitiveElements(model) else Future.unit
    } yield {
      Unit
    }
  }

  private def queryIsPIISensitive(model: Model) = {
    for {
      isPiiSensitive <- Rdf.Query.ask(model, s"$queries/is-pii-sensitive.sparql".noProtocol)
      _              <- println(s"Is PII Sensitive: $isPiiSensitive").wrapFuture
    } yield {
      isPiiSensitive
    }
  }

  private def queryExposure(model: Model) = {
    for {
      exposureResult <- Rdf.Query.select(model, s"$queries/pii-sensitive-exposure.sparql".noProtocol)
    } yield {
      val solution  = exposureResult.next()
      val total     = solution.getLiteral("domainElementCount").getInt
      val sensitive = solution.getLiteral("sensitiveElementCount").getInt
      val exposure  = sensitive.toFloat / total.toFloat
      println {
        s"""
        |Total sensitive elements: $sensitive 
        |Total domain elements: $total
        |Exposure: ${exposure.asPercentage}
        |""".stripMargin
      }
    }
  }

  private def querySensitiveElements(model: Model) = {
    for {
      sensitiveElements <- Rdf.Query.select(model, s"$queries/list-pii-sensitive.sparql".noProtocol)
      _                 <- Rdf.IO.print(sensitiveElements)
    } yield {
      Unit
    }
  }
}
