package pipeline.producer
import utils.jena.IO
import utils.jena.Lang.JsonLd
import org.apache.jena.rdf.model.Model
import utils.Conversions.{Fut, Url}
import utils.amf.Amf
import utils.ec.HasExecutionContext

import scala.concurrent.Future

case class AMFProducer(fileUrl: String) extends AsyncProducer with HasExecutionContext {
  override def produce(): Future[Model] = {
    for {
      (parsed, spec) <- Amf.parse(fileUrl)
      resolved       <- Amf.resolve(parsed, spec)
      _              <- Amf.render(resolved, fileUrl.withExtension(".jsonld"))
      rdf            <- IO.read(fileUrl.withProtocol("").withExtension(".jsonld"), JsonLd).wrapFuture
    } yield {
      rdf
    }
  }
}
