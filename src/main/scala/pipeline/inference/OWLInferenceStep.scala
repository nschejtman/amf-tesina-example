package pipeline.inference
import utils.jena._
import org.apache.jena.rdf.model.{Model, ModelFactory}
import org.apache.jena.reasoner.Reasoner
import utils.Conversions.Url
import utils.logger.WithDefaultLogger

case class OWLInferenceStep(ontologyUrl: String, lang: Lang, reasoner: Reasoner = Reasoners.default())
    extends SyncInferenceStep
    with WithDefaultLogger {

  override def run(model: Model): Model = {
    val schema = IO.read(ontologyUrl.withProtocol(""), lang)
    InferenceModel.from(model, schema, reasoner)
  }
}
