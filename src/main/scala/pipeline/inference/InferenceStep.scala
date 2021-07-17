package pipeline.inference

import org.apache.jena.rdf.model.Model

import scala.concurrent.Future

/** Step that enhances the model with new triples
  */
sealed trait InferenceStep[R] {
  def run(model: Model): R
}

trait SyncInferenceStep extends InferenceStep[Model] {
  def run(model: Model): Model
}

trait AsyncInferenceStep extends InferenceStep[Future[Model]] {
  def run(model: Model): Future[Model]
}
