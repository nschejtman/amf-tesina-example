package pipeline.producer
import org.apache.jena.rdf.model.Model
import utils.ec.HasExecutionContext
import utils.jena.{IO, Lang}
import utils.Conversions.{Url, Fut}

import scala.concurrent.Future

case class JenaProducer(fileUrl: String, lang: Lang) extends AsyncProducer with HasExecutionContext {
  override def produce(): Future[Model] = IO.read(fileUrl.withProtocol(""), lang).wrapFuture
}
