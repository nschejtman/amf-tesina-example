package utils.amf

import amf.apicontract.client.scala._
import amf.core.client.scala.config.RenderOptions
import amf.core.client.scala.model.document.BaseUnit
import amf.core.internal.remote.Spec
import utils.Conversions.Url

import java.io.FileWriter
import scala.concurrent.Await
import scala.concurrent.duration.Duration

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

  def parse(fileUrl: String): (BaseUnit, Spec) = {
    val result = Await.result(client().parse(fileUrl), Duration.Inf)
    if (!result.conforms) {
      throw new IllegalArgumentException(s"$fileUrl has parsing errors")
    } else {
      (result.baseUnit, result.sourceSpec)
    }
  }

  def resolve(baseUnit: BaseUnit, spec: Spec): BaseUnit = {
    val result = client(spec).transform(baseUnit)
    if (!result.conforms) {
      throw new IllegalArgumentException(s"${baseUnit.location()} has resolution errors")
    }
    result.baseUnit
  }

  def render(baseUnit: BaseUnit, url: String): Unit = {
    val content = client().render(baseUnit, "application/ld+json")
    val writer  = new FileWriter(url.withProtocol(""))
    writer.write(content)
    writer.close()
  }
}
