import scala.concurrent.ExecutionContext
trait HasGlobalEC {
  protected implicit val ec: ExecutionContext = scala.concurrent.ExecutionContext.Implicits.global
}
