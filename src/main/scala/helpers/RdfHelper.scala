package helpers
import amf.client.model.document.BaseUnit
import org.apache.jena.rdf.model.{InfModel, Model, ModelFactory}
import org.apache.jena.reasoner.ReasonerRegistry
import scala.collection.JavaConverters.mapAsJavaMapConverter

import java.io.FileWriter
import scala.concurrent.Future

object RdfHelper {
  def getInferenceModel(data: Model, schema: Model): Future[InfModel] = {
    val reasoner = ReasonerRegistry.getOWLReasoner.bindSchema(schema)
    Future.successful {
      ModelFactory.createInfModel(reasoner, data)
    }
  }

  def write(model: Model, fileName: String, lang: String, base: String): Unit = {
    println(s"Started: write $fileName")
    model.write(new FileWriter(fileName), lang, base)
    println(s"Done: write $fileName")
  }

  def toRdfModel(baseUnit: BaseUnit, namespaces: Map[String, String]): Future[Model] = Future.successful {
    println(s"Started: toRdfModel ${baseUnit.id}")
    val result = baseUnit.toNativeRdfModel().native().asInstanceOf[Model].setNsPrefixes(namespaces.asJava)
    println(s"Done: toRdfModel ${baseUnit.id}")
    result
  }

}
