package utils.amf

import amf.client.AMF
import com.typesafe.scalalogging.Logger
import utils.ec.HasExecutionContext

import scala.concurrent.Future

object InitializationHelper extends HasExecutionContext {
  def init()(implicit logger: Logger): Future[Unit] = {
    logger.debug(s"Started: AMF initialization")
    for {
      _ <- Future.successful(AMF.init().get())
    } yield {
      logger.debug(s"Done: AMF initialization")
    }
  }
}
