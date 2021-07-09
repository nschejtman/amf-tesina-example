import com.typesafe.scalalogging.Logger

trait HasLogger {
  protected implicit val logger: Logger = Logger[this.type]
}
