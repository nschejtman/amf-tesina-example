package pipeline.inference
import org.apache.jena.rdf.model.Model
import org.apache.jena.reasoner.Reasoner
import utils.Conversions.Url
import utils.jena._

case class OWLInferenceStep(ontologyUrl: String, lang: Lang, reasoner: Reasoner = Reasoners.default())
    extends SyncInferenceStep {

  override def run(model: Model): Model = {
    val schema = IO.read(ontologyUrl.withProtocol(""), lang)
    InferenceModel.from(model, schema, reasoner)
  }
}
