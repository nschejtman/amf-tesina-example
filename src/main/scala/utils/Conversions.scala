package utils
import scala.concurrent.Future
import scala.math.pow

object Conversions {
  implicit class Url(val url: String) extends AnyVal {
    def withProtocol(protocol: String): String = url.replaceFirst("[a-zA-Z]+://", protocol)

    def withExtension(extension: String): String = {
      val i = url.lastIndexOf(".")
      url.substring(0, i) + extension
    }
  }

  implicit class Fut[T](t: T) {
    def wrapFuture: Future[T] = Future.successful(t)
  }

  implicit class Percentage(val d: Double) extends AnyVal {
    def asPercentage: String = s"${d * 100}%"
  }

  implicit class RichFloat(val f: Float) extends AnyVal {
    def roundTo(decimals: Int): Float = {
      val m = 10 ** decimals
      (f * m).round.toFloat / m
    }
  }

  implicit class RichInt(val i: Int) extends AnyVal {
    def **(exp: Int): Int = pow(i, exp).toInt
  }
}
