package pipeline.producer

import org.apache.jena.rdf.model.Model

/** Pipeline input producer
  */
trait Producer {
  def produce(): Model
}
