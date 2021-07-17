package utils
import scala.concurrent.Future

object Conversions {
  implicit class Url(url: String) {
    def noProtocol: String = url.replaceFirst("[a-zA-Z]+://", "")
    def withExtension(extension: String): String = {
      val i = url.lastIndexOf(".")
      url.substring(0, i) + extension
    }
  }

  implicit class Fut[T](t: T) {
    def wrapFuture: Future[T] = Future.successful(t)
  }

  implicit class Percentage(d: Double) {
    def asPercentage: String = s"${d * 100}%"
  }
}
