package pipeline.transformer

import org.apache.jena.rdf.model.Model
import utils.jena.Query

case class ConstructQueryTransformer(queryUrl: String) extends Transformer {
  override def transform(model: Model): Model = Query.construct(model, queryUrl)
}
