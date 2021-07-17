package pipeline.consumer

import utils.jena.Query
import org.apache.jena.query.ResultSet
import org.apache.jena.rdf.model.Model

case class AskConsumer(queryUrl: String) extends SyncConsumer[Boolean] {
  override def consume(model: Model): Boolean = Query.ask(model, queryUrl)
}

case class SelectConsumer(queryUrl: String) extends SyncConsumer[ResultSet] {
  override def consume(model: Model): ResultSet = Query.select(model, queryUrl)
}
