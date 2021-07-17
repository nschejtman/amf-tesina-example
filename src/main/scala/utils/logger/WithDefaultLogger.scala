package utils.logger
import com.typesafe.scalalogging.Logger

trait WithDefaultLogger extends HasLogger {
  protected implicit val logger: Logger = Logger[this.type]
}
