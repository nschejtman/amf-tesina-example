package pipeline.transformer

import org.apache.jena.rdf.model.Model
import utils.jena.Query

case class ConstructQueryTransformer(queryUrl: String, additive: Boolean) extends Transformer {
  override def transform(model: Model): Model = {
    val result = Query.construct(model, queryUrl)
    if (additive) model.union(result)
    else result
  }
}
