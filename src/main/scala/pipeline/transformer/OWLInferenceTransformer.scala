package pipeline.transformer
import org.apache.jena.rdf.model.Model
import org.apache.jena.reasoner.Reasoner
import utils.Conversions.Url
import utils.jena._

case class OWLInferenceTransformer(ontologyUrl: String, lang: Lang, reasoner: Reasoner = Reasoners.default())
    extends Transformer {

  override def transform(model: Model): Model = {
    val schema = IO.read(ontologyUrl.withProtocol(""), lang)
    InferenceModel.from(model, schema, reasoner)
  }
}
