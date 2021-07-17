package utils.amf

import amf.client.model.document.BaseUnit
import amf.client.parse.Parser
import amf.client.render.{RenderOptions, Renderer}
import amf.client.resolve.Resolver
import amf.core.remote.Vendor
import com.typesafe.scalalogging.Logger
import utils.Conversions.Fut

import scala.concurrent.Future

object Amf {
  def parse(fileUrl: String, vendor: Vendor)(implicit logger: Logger): Future[BaseUnit] = {
    logger.debug(s"Started: parse $fileUrl")
    val parser = new Parser(vendor.name, MediaType.forVendor(vendor))
    val result = parser.parseFileAsync(fileUrl).get()
    logger.debug(s"Done: parse $fileUrl")
    result.wrapFuture
  }

  def resolve(baseUnit: BaseUnit, vendor: Vendor)(implicit logger: Logger): Future[BaseUnit] = {
    logger.debug(s"Started: resolve ${baseUnit.id}")
    val resolver = new Resolver(vendor.name)
    val result   = resolver.resolve(baseUnit)
    logger.debug(s"Done: resolve ${baseUnit.id}")
    result.wrapFuture
  }

  def render(baseUnit: BaseUnit, url: String)(implicit logger: Logger): Future[Unit] = {
    logger.debug(s"Started: render ${baseUnit.id}")
    val renderer      = new Renderer(Vendor.AMF.name, "application/ld+json")
    val renderOptions = RenderOptions().withFlattenedJsonLd.withCompactUris.withPrettyPrint.withSourceMaps
    renderer.generateFile(baseUnit, url, renderOptions).get()
    logger.debug(s"Done: render ${baseUnit.id}")
    Future.unit
  }
}
