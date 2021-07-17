package pipeline.producer

import org.apache.jena.rdf.model.Model

import scala.concurrent.Future

sealed trait Producer

/** Pipeline input producer
  */
trait SyncProducer extends Producer {
  def produce(): Model
}

trait AsyncProducer extends Producer {
  def produce(): Future[Model]
}
