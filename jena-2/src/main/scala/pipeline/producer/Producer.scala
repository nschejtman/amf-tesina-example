package pipeline.producer

import com.hp.hpl.jena.rdf.model.Model

/** Pipeline input producer
  */
trait Producer {
  def produce(): Model
}
