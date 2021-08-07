package utils.amf

import amf.apicontract.client.scala._
import amf.core.client.scala.config.RenderOptions
import amf.core.client.scala.model.document.BaseUnit
import amf.core.internal.remote.Spec
import com.typesafe.scalalogging.Logger
import utils.Conversions.{Fut, Url}

import java.io.FileWriter
import scala.concurrent.{ExecutionContext, Future}

object Amf {
  private def client(spec: Spec = null): AMFBaseUnitClient = {
    val baseConfig = spec match {
      case Spec.RAML08  => RAMLConfiguration.RAML08()
      case Spec.RAML10  => RAMLConfiguration.RAML10()
      case Spec.OAS20   => OASConfiguration.OAS20()
      case Spec.OAS30   => OASConfiguration.OAS30()
      case Spec.ASYNC20 => AsyncAPIConfiguration.Async20()
      case null         => WebAPIConfiguration.WebAPI()
    }
    baseConfig
      .withRenderOptions(RenderOptions().withPrettyPrint.withCompactUris.withSourceMaps)
      .baseUnitClient()
  }

  def parse(fileUrl: String)(implicit logger: Logger, executionContext: ExecutionContext): Future[(BaseUnit, Spec)] = {
    logger.debug(s"Started: parse $fileUrl")

    client().parse(fileUrl).map { result =>
      if (!result.conforms) {
        throw new IllegalArgumentException(s"$fileUrl has parsing errors")
      } else {
        logger.debug(s"Done: parse $fileUrl")
        (result.baseUnit, result.sourceSpec)
      }
    }
  }

  def resolve(baseUnit: BaseUnit, spec: Spec)(implicit logger: Logger): Future[BaseUnit] = {
    logger.debug(s"Started: resolve ${baseUnit.id}")
    val result = client(spec).transform(baseUnit)
    logger.debug(s"Done: resolve ${baseUnit.id}")
    if (!result.conforms) {
      throw new IllegalArgumentException(s"${baseUnit.location()} has resolution errors")
    }
    result.baseUnit.wrapFuture
  }

  def render(baseUnit: BaseUnit, url: String)(implicit logger: Logger): Future[Unit] = {
    logger.debug(s"Started: render ${baseUnit.id}")
    val content = client().render(baseUnit, "application/ld+json")
    val writer  = new FileWriter(url.withProtocol(""))
    writer.write(content)
    writer.close()
    logger.debug(s"Done: render ${baseUnit.id}")
    Future.unit
  }
}
