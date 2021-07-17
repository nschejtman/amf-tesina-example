package pipeline.producer
import amf.core.remote.Vendor
import utils.jena.IO
import utils.jena.Lang.JsonLd
import org.apache.jena.rdf.model.Model
import utils.Conversions.{Fut, Url}
import utils.amf.{Amf, InitializationHelper}
import utils.ec.HasExecutionContext
import utils.logger.WithDefaultLogger

import scala.concurrent.Future

case class AMFProducer(fileUrl: String, vendor: Vendor) extends AsyncProducer with HasExecutionContext with WithDefaultLogger {
  override def produce(): Future[Model] = {
    for {
      _        <- InitializationHelper.init()
      parsed   <- Amf.parse(fileUrl, vendor)
      resolved <- Amf.resolve(parsed, vendor)
      _        <- Amf.render(resolved, fileUrl.withExtension(".jsonld"))
      rdf      <- IO.read(fileUrl.withProtocol("").withExtension(".jsonld"), JsonLd).wrapFuture
    } yield {
      rdf
    }
  }
}
