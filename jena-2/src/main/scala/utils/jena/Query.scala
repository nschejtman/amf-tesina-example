package utils.jena
import com.hp.hpl.jena.query.{QueryExecutionFactory, QueryFactory, ResultSet}
import com.hp.hpl.jena.rdf.model.Model
import utils.Conversions.Url

object Query {
  private def executionFrom(model: Model, queryUrl: String) = {
    val query = QueryFactory.read(queryUrl.withProtocol(""))
    QueryExecutionFactory.create(query, model)
  }

  def select(model: Model, queryUrl: String): ResultSet = executionFrom(model, queryUrl).execSelect()

  def construct(model: Model, queryUrl: String): Model = executionFrom(model, queryUrl).execConstruct()

  def ask(model: Model, queryUrl: String): Boolean = executionFrom(model, queryUrl).execAsk()

  def describe(model: Model, queryUrl: String): Model = executionFrom(model, queryUrl).execDescribe()

}
