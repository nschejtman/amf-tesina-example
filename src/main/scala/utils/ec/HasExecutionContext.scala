package utils.ec
import scala.concurrent.ExecutionContext

trait HasExecutionContext {
  protected implicit val ec: ExecutionContext = scala.concurrent.ExecutionContext.Implicits.global
}
