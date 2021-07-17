package pipeline.consumer

import org.apache.jena.rdf.model.Model

import scala.concurrent.Future

/** Step that consumer a model without changing it (make assertions, calculate metrics)
  */
sealed trait Consumer[R] {
  def consume(model: Model): R
}

trait AsyncConsumer[T] extends Consumer[Future[T]]

trait SyncConsumer[T]  extends Consumer[T]
