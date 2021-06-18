package helpers
import amf.client.model.document.BaseUnit
import com.typesafe.scalalogging.Logger
import helpers.Conversions.Fut
import openllet.jena.PelletReasonerFactory
import org.apache.jena.query.{QueryExecutionFactory, QueryFactory, ResultSet}
import org.apache.jena.rdf.model.{InfModel, Literal, Model, ModelFactory, Resource}
import org.apache.jena.reasoner.ReasonerRegistry

import java.io.FileWriter
import scala.collection.JavaConverters.mapAsJavaMapConverter
import scala.concurrent.Future

object Rdf {
  object Inference {
    def default(data: Model, schema: Model): Future[InfModel] = {
      val reasoner = ReasonerRegistry.getOWLReasoner.bindSchema(schema)
      ModelFactory.createInfModel(reasoner, data).wrapFuture
    }

    def pellet(ontology: Model, data: Model): Future[InfModel] = {
      val reasoner = PelletReasonerFactory.theInstance().create()
      reasoner.bind(ontology)
      ModelFactory.createInfModel(reasoner, data).wrapFuture
    }
  }

  object IO {
    val DIVIDER = "-----------------------------------------------------------------------------------------------------------------"

    def read(fileName: String, lang: String = "JSON-LD")(implicit logger: Logger): Future[Model] = {
      logger.debug(s"Started: read $fileName")
      val model = ModelFactory.createDefaultModel()
      model.read(fileName, lang)
      logger.debug(s"Done: read $fileName")
      model.wrapFuture
    }

    def write(model: Model, fileName: String, lang: String, base: String)(implicit logger: Logger): Future[Unit] = {
      logger.debug(s"Started: write $fileName")
      model.write(new FileWriter(fileName), lang, base)
      logger.debug(s"Done: write $fileName")
      Future.unit
    }

    def print(model: Model, lang: String = "JSON-LD"): Future[Unit] = {
      model.write(System.out, "JSON-LD")
      Future.unit
    }

    def print(resultSet: ResultSet): Future[Unit] = {
      val vars = resultSet.getResultVars
      var i    = 0
      println(DIVIDER)
      resultSet.forEachRemaining { result =>
        println(s"Result: $i")
        vars.forEach { variable =>
          val value = result.get(variable)
          println(s"$variable: $value")
        }
        i += 1
        println(DIVIDER)
      }
      Future.unit
    }
  }

  object AMF {
    def toRdfModel(baseUnit: BaseUnit, namespaces: Map[String, String])(implicit logger: Logger): Future[Model] = {
      logger.debug(s"Started: toRdfModel ${baseUnit.id}")
      val result = baseUnit.toNativeRdfModel().native().asInstanceOf[Model].setNsPrefixes(namespaces.asJava)
      logger.debug(s"Done: toRdfModel ${baseUnit.id}")
      result.wrapFuture
    }
  }

  object Query {
    def select(model: Model, queryUrl: String): Future[ResultSet] = {
      val query     = QueryFactory.read(queryUrl)
      val execution = QueryExecutionFactory.create(query, model)
      execution.execSelect().wrapFuture
    }

    def construct(model: Model, queryUrl: String): Future[Model] = {
      val query     = QueryFactory.read(queryUrl)
      val execution = QueryExecutionFactory.create(query, model)
      execution.execConstruct().wrapFuture
    }

    def ask(model: Model, queryUrl: String): Future[Boolean] = {
      val query     = QueryFactory.read(queryUrl)
      val execution = QueryExecutionFactory.create(query, model)
      execution.execAsk().wrapFuture
    }
  }

}
