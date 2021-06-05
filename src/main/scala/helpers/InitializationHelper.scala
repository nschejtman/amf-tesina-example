package helpers
import amf.plugins.document.Vocabularies
import amf.plugins.document.vocabularies.AMLPlugin
import amf.plugins.features.validation.AMFValidatorPlugin
import scala.concurrent.ExecutionContext.Implicits.global

import scala.concurrent.Future

object InitializationHelper {
  def init(): Future[Unit] = {
    println(s"Started: AML initialization")
    for {
      _ <- amf.core.AMF.init()
      _ <- Future.successful(Vocabularies.register())
      _ <- AMLPlugin.init()
      _ <- Future.successful(amf.core.AMF.registerPlugin(AMFValidatorPlugin))
      _ <- AMFValidatorPlugin.init()
    } yield {
      println(s"Done: AML initialization")
    }
  }

}
