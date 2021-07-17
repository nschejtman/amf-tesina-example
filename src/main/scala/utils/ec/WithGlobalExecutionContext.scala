package utils.ec
import scala.concurrent.ExecutionContext

trait WithGlobalExecutionContext {
  protected implicit val ec: ExecutionContext = scala.concurrent.ExecutionContext.Implicits.global
}
