package utils.jena
import amf.client.model.document.BaseUnit
import com.typesafe.scalalogging.Logger
import openllet.jena.PelletReasonerFactory
import org.apache.jena.rdf.model.{InfModel, Model, ModelFactory}
import org.apache.jena.reasoner.{Reasoner, ReasonerRegistry}

import scala.collection.JavaConverters.mapAsJavaMapConverter

object Rdf {


  object InferenceModel {
    def default(data: Model, schema: Model): InfModel = {
      val reasoner = ReasonerRegistry.getOWLReasoner.bindSchema(schema)
      ModelFactory.createInfModel(reasoner, data)
    }

    def pellet(ontology: Model, data: Model): InfModel = {
      val reasoner = PelletReasonerFactory.theInstance().create()
      reasoner.bind(ontology)
      ModelFactory.createInfModel(reasoner, data)
    }
  }

  object AMF {
    def toRdfModel(baseUnit: BaseUnit, namespaces: Map[String, String])(implicit logger: Logger): Model = {
      logger.debug(s"Started: toRdfModel ${baseUnit.id}")
      val result = baseUnit.toNativeRdfModel().native().asInstanceOf[Model].setNsPrefixes(namespaces.asJava)
      logger.debug(s"Done: toRdfModel ${baseUnit.id}")
      result
    }
  }

}
