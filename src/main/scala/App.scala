import amf.client.model.document.BaseUnit
import amf.client.parse.Aml10Parser
import amf.plugins.document.Vocabularies
import amf.plugins.document.vocabularies.AMLPlugin
import amf.plugins.features.validation.AMFValidatorPlugin
import org.apache.jena.rdf.model.Model

import java.io.FileWriter
import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.duration.Duration
import scala.concurrent.{Await, Future}
import scala.util.{Failure, Success}

//noinspection SameParameterValue
object App {
  def main(args: Array[String]): Unit = {
    val result: Future[Unit] = for {
      // Initialize the AMF framework (boilerplate code)
      _ <- init()

      // Parse the input files: dialect, vocabulary and example that will be mapped to RDF with the dialect mappings
      dialectBaseUnitModel        <- parse("file://src/main/resources/agents/agents.dialect.yaml")
      vocabularyBaseUnitUnitModel <- parse("file://src/main/resources/agents/agents.vocabulary.yaml")
      exampleBaseUnitModel        <- parse("file://src/main/resources/agents/examples/agents.example.yaml")

      /**
        * Transform AMF's native Base Unit representation to RDF
        * -----
        * In AMF we have our own internal model which is based on RDF but is easier to consume by traditional programming
        * languages. We can convert it nevertheless to an RDF representation
        */
      dialectRdfModel    <- toRdfModel(dialectBaseUnitModel)
      vocabularyRdfModel <- toRdfModel(vocabularyBaseUnitUnitModel)
      exampleRdfModel    <- toRdfModel(exampleBaseUnitModel)

    } yield {
      case class Fmt(lang: String, extension: String)
      val formats = Fmt("JSON-LD", ".jsonld") :: Fmt("RDF/XML", ".xml") :: Fmt("TTL", ".ttl") :: Nil
      formats.foreach {
        case Fmt(lang, extension) =>
          write(dialectRdfModel, s"src/main/resources/agents/parsed/agents.dialect$extension", lang)
          write(vocabularyRdfModel, s"src/main/resources/agents/parsed/agents.vocabulary$extension", lang)
          write(exampleRdfModel, s"src/main/resources/agents/parsed/agents.example$extension", lang)
      }
    }

    result onComplete {
      case Success(_) => println("Finished with success")
      case Failure(_) => println("Finished with failure")
    }

    Await.ready(result, Duration.Inf)
  }

  private def write(model: Model, fileName: String, lang: String): Unit = {
    println(s"Started: write $fileName")
    model.write(new FileWriter(fileName), lang)
    println(s"Done: write $fileName")
  }

  private def toRdfModel(baseUnit: BaseUnit): Future[Model] = Future.successful {
    println(s"Started: toRdfModel ${baseUnit.id}")
    val result = baseUnit.toNativeRdfModel().native().asInstanceOf[Model]
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
