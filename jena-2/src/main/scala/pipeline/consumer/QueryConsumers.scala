package pipeline.consumer

import utils.jena.Query
import com.hp.hpl.jena.query.ResultSet
import com.hp.hpl.jena.rdf.model.Model

case class AskConsumer(queryUrl: String) extends Consumer[Boolean] {
  override def consume(model: Model): Boolean = Query.ask(model, queryUrl)
}

case class SelectConsumer(queryUrl: String) extends Consumer[ResultSet] {
  override def consume(model: Model): ResultSet = Query.select(model, queryUrl)
}
