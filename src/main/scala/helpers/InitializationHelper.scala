package helpers
import amf.client.AMF
import com.typesafe.scalalogging.Logger

import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.Future

object InitializationHelper {
  def init()(implicit logger: Logger): Future[Unit] = {
    logger.debug(s"Started: AMF initialization")
    for {
      _ <- Future.successful(AMF.init().get())
    } yield {
      logger.debug(s"Done: AMF initialization")
    }
  }
}
