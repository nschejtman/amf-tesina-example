import amf.client.model.document.{BaseUnit, Vocabulary}
import amf.client.parse.Aml10Parser
import amf.plugins.document.Vocabularies
import amf.plugins.document.vocabularies.AMLPlugin
import amf.plugins.features.validation.AMFValidatorPlugin
import org.apache.jena.rdf.model.{InfModel, Model, ModelFactory}
import org.apache.jena.reasoner.ReasonerRegistry

import java.io.FileWriter
import scala.collection.JavaConverters.mapAsJavaMapConverter
import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.duration.Duration
import scala.concurrent.{Await, Future}
import scala.util.{Failure, Success}

//noinspection SameParameterValue
object AmlExample {
  def main(args: Array[String]): Unit = {
    val result: Future[Unit] = for {
      // Initialize the AMF framework (boilerplate code)
      _ <- init()

      // Parse the input files: dialect, vocabulary and example that will be mapped to RDF with the dialect mappings
      dialectBaseUnitModel        <- parse("file://src/main/resources/agents/agents.dialect.yaml")
      vocabularyBaseUnitUnitModel <- parse("file://src/main/resources/agents/agents.vocabulary.yaml")
      instanceBaseUnitModel       <- parse("file://src/main/resources/agents/example-instances/agents.instance.yaml")
      namespaces <- Future.successful {
        Map(
            "rdf"        -> "http://www.w3.org/1999/02/22-rdf-syntax-ns#",
            "rdfs"       -> "http://www.w3.org/2000/01/rdf-schema#",
            "owl"        -> "http://www.w3.org/2002/07/owl#",
            "shacl"      -> "http://www.w3.org/ns/shacl#",
            "xsd"        -> "http://www.w3.org/2001/XMLSchema#",
            "document"   -> "http://a.ml/vocabularies/document#",
            "meta"       -> "http://a.ml/vocabularies/meta#",
            "core"       -> "http://a.ml/vocabularies/core#",
            "vocabulary" -> vocabularyBaseUnitUnitModel.asInstanceOf[Vocabulary].base.value(),
            "dialect"    -> s"${dialectBaseUnitModel.id}#/"
        )
      }

      /**
        * Transform AMF's native Base Unit representation to RDF
        * -----
        * In AMF we have our own internal model which is based on RDF but is easier to consume by traditional programming
        * languages. We can convert it nevertheless to an RDF representation
        */
      dialectRdfModel           <- toRdfModel(dialectBaseUnitModel, namespaces)
      vocabularyRdfModel        <- toRdfModel(vocabularyBaseUnitUnitModel, namespaces)
      instanceRdfModel          <- toRdfModel(instanceBaseUnitModel, namespaces)
      instanceInferenceRdfModel <- getInferenceModel(instanceRdfModel, vocabularyRdfModel)
      inferredTriples <- Future.successful {
        instanceInferenceRdfModel.difference(instanceRdfModel)
      }

    } yield {
      case class Fmt(lang: String, extension: String)
      val formats = Fmt("JSON-LD", ".jsonld") :: Fmt("RDF/XML", ".xml") :: Fmt("TTL", ".ttl") :: Nil

      formats.foreach {
        case Fmt(lang, extension) =>
          write(dialectRdfModel, s"src/main/resources/agents/graphs/agents.dialect$extension", lang, dialectBaseUnitModel.id)
          write(vocabularyRdfModel, s"src/main/resources/agents/graphs/agents.vocabulary$extension", lang, vocabularyBaseUnitUnitModel.id)
          write(instanceRdfModel, s"src/main/resources/agents/graphs/agents.instance$extension", lang, instanceBaseUnitModel.id)
          write(instanceInferenceRdfModel,
                s"src/main/resources/agents/graphs-with-reasoning/agents.instance$extension",
                lang, instanceBaseUnitModel.id)
          write(inferredTriples,
                s"src/main/resources/agents/graphs-with-reasoning/agents.inferred.triples.instance$extension",
                lang, instanceBaseUnitModel.id)

      }
    }

    result onComplete {
      case Success(_) => println("Finished with success")
      case Failure(f) => println(s"Finished with failure: ${f.toString}")
    }

    Await.ready(result, Duration.Inf)
  }

  private def getInferenceModel(data: Model, schema: Model): Future[InfModel] = {
    val reasoner = ReasonerRegistry.getOWLReasoner.bindSchema(schema)
    Future.successful {
      ModelFactory.createInfModel(reasoner, data)
    }
  }

  private def write(model: Model, fileName: String, lang: String, base: String): Unit = {
    println(s"Started: write $fileName")
    model.write(new FileWriter(fileName), lang, base)
    println(s"Done: write $fileName")
  }

  private def toRdfModel(baseUnit: BaseUnit, namespaces: Map[String, String]): Future[Model] = Future.successful {
    println(s"Started: toRdfModel ${baseUnit.id}")
    val result = baseUnit.toNativeRdfModel().native().asInstanceOf[Model].setNsPrefixes(namespaces.asJava)
    println(s"Done: toRdfModel ${baseUnit.id}")
    result
  }

  private def parse(fileUrl: String): Future[BaseUnit] = {
    Future.successful {
      println(s"Started: parse $fileUrl")
      val parser = new Aml10Parser()
      val result = parser.parseFileAsync(fileUrl).get()
      println(s"Done: parse $fileUrl")
      result
    }
  }

  def init(): Future[Unit] = {
    println(s"Started: AML initialization")
    for {
      _ <- amf.core.AMF.init()
      _ <- Future.successful(Vocabularies.register())
      _ <- AMLPlugin.init()
      _ <- Future.successful(amf.core.AMF.registerPlugin(AMFValidatorPlugin))
      _ <- AMFValidatorPlugin.init()
    } yield {
      println(s"Done: AML initialization")
    }
  }

}
