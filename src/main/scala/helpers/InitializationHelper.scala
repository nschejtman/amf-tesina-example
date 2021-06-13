package helpers
import amf.client.AMF

import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.Future

object InitializationHelper {
  def init(): Future[Unit] = {
    println(s"Started: AMF initialization")
    for {
      _ <- Future.successful(AMF.init().get())
    } yield {
      println(s"Done: AMF initialization")
    }
  }
}
