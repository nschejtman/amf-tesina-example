package helpers
import amf.client.model.document.BaseUnit
import helpers.Conversions.Fut
import org.apache.jena.rdf.model.{InfModel, Model, ModelFactory}
import org.apache.jena.reasoner.ReasonerRegistry

import java.io.FileWriter
import scala.collection.JavaConverters.mapAsJavaMapConverter
import scala.concurrent.Future

object RdfHelper {
  def getInferenceModel(data: Model, schema: Model): Future[InfModel] = {
    val reasoner = ReasonerRegistry.getOWLReasoner.bindSchema(schema)
    ModelFactory.createInfModel(reasoner, data).wrapFuture
  }

  def read(fileName: String, lang: String = "JSON-LD"): Future[Model] = {
    println(s"Started: read $fileName")
    val model = ModelFactory.createDefaultModel()
    model.read(fileName, lang)
    println(s"Done: read $fileName")
    model.wrapFuture
  }

  def write(model: Model, fileName: String, lang: String, base: String): Future[Unit] = {
    println(s"Started: write $fileName")
    model.write(new FileWriter(fileName), lang, base)
    println(s"Done: write $fileName")
    Future.unit
  }

  def toRdfModel(baseUnit: BaseUnit, namespaces: Map[String, String]): Future[Model] = {
    println(s"Started: toRdfModel ${baseUnit.id}")
    val result = baseUnit.toNativeRdfModel().native().asInstanceOf[Model].setNsPrefixes(namespaces.asJava)
    println(s"Done: toRdfModel ${baseUnit.id}")
    result.wrapFuture
  }

}
