import amf.core.remote.{Raml10, Vendor}
import helpers.Conversions._
import helpers.Rdf
import org.apache.jena.rdf.model.Model

import scala.concurrent.Future

//noinspection SameParameterValue
object PIISensitiveExample extends Example {

  def main(args: Array[String]): Unit = mainImpl()

  override protected def obtainModelFromAmf(fileUrl: String, vendor: Vendor): Future[Model] = {
    Rdf.IO.read(fileUrl.noProtocol.withExtension(".jsonld"))
  }

  override protected def runQueriesOn(model: Model, fileUrl: String): Future[Unit] = {
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
  override protected def kernel(): Future[Unit] = run(s"$raml/pii-sensitive/api.raml", s"$ontologies/PII.ontology.ttl", Raml10)
}
