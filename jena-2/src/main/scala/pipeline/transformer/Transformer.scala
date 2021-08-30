package pipeline.transformer

import com.hp.hpl.jena.rdf.model.Model

/** Step that enhances the model with new triples
  */
trait Transformer {
  def transform(model: Model): Model
}
