package pipeline.producer
import utils.jena.IO
import utils.jena.Lang.JsonLd
import org.apache.jena.rdf.model.Model
import utils.Conversions.Url
import utils.amf.Amf

case class AMFProducer(fileUrl: String) extends Producer {
  override def produce(): Model = {
    val (parsed, spec) = Amf.parse(fileUrl)
    val resolved       = Amf.resolve(parsed, spec)
    Amf.render(resolved, fileUrl.withExtension(".jsonld"))
    IO.read(fileUrl.withProtocol("").withExtension(".jsonld"), JsonLd)
  }
}
