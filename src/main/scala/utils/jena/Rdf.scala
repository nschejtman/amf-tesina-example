package utils.jena
import amf.core.client.scala.model.document.BaseUnit
import amf.core.client.scala.rdf.RdfUnitConverter
import org.apache.jena.rdf.model.Model

object Rdf {
  object AMF {
    def toRdfModel(baseUnit: BaseUnit, namespaces: Map[String, String]): Model = {
      JenaLock.synchronized {
        val result = RdfUnitConverter.toNativeRdfModel(baseUnit).native()
        result.asInstanceOf[Model]
      }
    }
  }

}
