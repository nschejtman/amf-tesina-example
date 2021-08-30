package pipeline.transformer
import com.hp.hpl.jena.rdf.model.Model
import com.hp.hpl.jena.reasoner.Reasoner
import utils.Conversions.Url
import utils.jena._

case class OWLInferenceTransformer(ontologyUrl: String, lang: Lang, reasoner: Reasoner = Reasoners.default())
    extends Transformer {

  override def transform(model: Model): Model = {
    val schema = IO.read(ontologyUrl.withProtocol(""), lang)
    InferenceModel.from(model, schema, reasoner)
  }
}
