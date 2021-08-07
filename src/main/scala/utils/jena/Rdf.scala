package utils.jena
import amf.core.client.scala.model.document.BaseUnit
import amf.core.client.scala.rdf.RdfUnitConverter
import amf.core.internal.rdf.RdfModelDocument
import com.typesafe.scalalogging.Logger
import openllet.jena.PelletReasonerFactory
import org.apache.jena.rdf.model.{InfModel, Model, ModelFactory}
import org.apache.jena.reasoner.{Reasoner, ReasonerRegistry}

import scala.collection.JavaConverters.mapAsJavaMapConverter

object Rdf {
  object AMF {
    def toRdfModel(baseUnit: BaseUnit, namespaces: Map[String, String])(implicit logger: Logger): Model = {
      logger.debug(s"Started: toRdfModel ${baseUnit.id}")
      val result = RdfUnitConverter.toNativeRdfModel(baseUnit).native()
      logger.debug(s"Done: toRdfModel ${baseUnit.id}")
      result.asInstanceOf[Model]
    }
  }

}
