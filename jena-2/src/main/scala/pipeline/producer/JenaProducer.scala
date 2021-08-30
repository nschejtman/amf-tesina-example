package pipeline.producer
import com.hp.hpl.jena.rdf.model.Model
import utils.jena.{IO, Lang}
import utils.Conversions.Url


case class JenaProducer(fileUrl: String, lang: Lang) extends Producer {
  override def produce(): Model = IO.read(fileUrl.withProtocol(""), lang)
}
