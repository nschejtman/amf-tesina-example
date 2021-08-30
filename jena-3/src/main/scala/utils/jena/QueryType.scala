package utils.jena

sealed trait QueryType

object QueryType {
  case object Select    extends QueryType
  case object Construct extends QueryType
  case object Ask       extends QueryType
  case object Describe  extends QueryType
}
