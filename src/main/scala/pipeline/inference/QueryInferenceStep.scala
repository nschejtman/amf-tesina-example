package pipeline.inference

import org.apache.jena.rdf.model.Model
import utils.jena.Query

trait QueryInferenceStep extends SyncInferenceStep {
  override def run(model: Model): Model = {
    val enrichment = query(model)
    model.union(enrichment)
  }
  def query(model: Model): Model
}

case class ConstructAndEnrich(queryUrl: String) extends QueryInferenceStep {
  override def query(model: Model): Model = Query.construct(model, queryUrl)
}
