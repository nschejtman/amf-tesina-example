package utils.amf

import amf.apicontract.client.scala._
import amf.core.client.scala.config.RenderOptions
import amf.core.client.scala.model.document.BaseUnit
import amf.core.internal.remote.Spec
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

  def parse(fileUrl: String)(implicit executionContext: ExecutionContext): Future[(BaseUnit, Spec)] = {

    client().parse(fileUrl).map { result =>
      if (!result.conforms) {
        throw new IllegalArgumentException(s"$fileUrl has parsing errors")
      } else {
        (result.baseUnit, result.sourceSpec)
      }
    }
  }

  def resolve(baseUnit: BaseUnit, spec: Spec): Future[BaseUnit] = {
    val result = client(spec).transform(baseUnit)
    if (!result.conforms) {
      throw new IllegalArgumentException(s"${baseUnit.location()} has resolution errors")
    }
    result.baseUnit.wrapFuture
  }

  def render(baseUnit: BaseUnit, url: String): Future[Unit] = {
    val content = client().render(baseUnit, "application/ld+json")
    val writer  = new FileWriter(url.withProtocol(""))
    writer.write(content)
    writer.close()
    Future.unit
  }
}
