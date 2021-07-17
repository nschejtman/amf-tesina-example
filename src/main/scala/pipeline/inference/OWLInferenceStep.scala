package pipeline.inference
import utils.jena._
import org.apache.jena.rdf.model.{Model, ModelFactory}
import org.apache.jena.reasoner.Reasoner
import utils.Conversions.Url
import utils.logger.WithDefaultLogger

case class OWLInferenceStep(ontologyUrl: String, lang: Lang) extends SyncInferenceStep with WithDefaultLogger {

  override def run(model: Model): Model = {
    val schema = IO.read(ontologyUrl.withProtocol(""), lang)
    ModelFactory.createInfModel(reasoner.bindSchema(schema), model)
  }

  def reasoner: Reasoner = Reasoners.default()
}
