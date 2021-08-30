package pipeline.consumer

import org.apache.jena.rdf.model.Model

/** Step that consumer a model without changing it (make assertions, calculate metrics)
  */
trait Consumer[R] {
  def consume(model: Model): R
}
