package utils.jena
import org.apache.jena.rdf.model.{InfModel, Model, ModelFactory}
import org.apache.jena.reasoner.Reasoner

object InferenceModel {
  def from(data: Model, schema: Model, reasoner: Reasoner): InfModel = JenaLock.synchronized {
    ModelFactory.createInfModel(reasoner, schema.union(data))
  }

}
