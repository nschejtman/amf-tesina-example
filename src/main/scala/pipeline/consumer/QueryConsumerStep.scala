package pipeline.consumer

import utils.jena.Query
import org.apache.jena.query.ResultSet
import org.apache.jena.rdf.model.Model

/** Creates a consumer for the return type of a query rather than a Model
  * @param consumer
  * @tparam R
  * @tparam O
  */
abstract class QueryConsumerStep[R] extends Consumer[R] {
  override def consume(model: Model): R = query(model)
  def query(model: Model): R
}

case class Ask(queryUrl: String) extends QueryConsumerStep[Boolean] {
  override def query(model: Model): Boolean = Query.ask(model, queryUrl)
}

case class Select(queryUrl: String) extends QueryConsumerStep[ResultSet] {
  override def query(model: Model): ResultSet = Query.select(model, queryUrl)
}
