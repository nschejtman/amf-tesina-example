package utils.jena
import com.hp.hpl.jena.rdf.model.{InfModel, Model, ModelFactory}
import com.hp.hpl.jena.reasoner.Reasoner

object InferenceModel {
  def from(data: Model, schema: Model, reasoner: Reasoner): InfModel = ModelFactory.createInfModel(reasoner, schema.union(data))
}
