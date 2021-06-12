import amf.client.model.document.{BaseUnit, Vocabulary}
import amf.client.parse.Aml10Parser
import helpers.Conversions.Fut
import helpers.{InitializationHelper, Namespaces, Rdf}

import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.duration.Duration
import scala.concurrent.{Await, Future}
import scala.util.{Failure, Success}

//noinspection SameParameterValue
object AmlExample {
  def main(args: Array[String]): Unit = {
    val result: Future[Unit] = for {
      // Initialize the AMF framework (boilerplate code)
      _ <- InitializationHelper.init()

      // Parse the input files: dialect, vocabulary and example that will be mapped to RDF with the dialect mappings
      dialectBaseUnitModel        <- parse("file://src/main/resources/agents/agents.dialect.yaml")
      vocabularyBaseUnitUnitModel <- parse("file://src/main/resources/agents/agents.vocabulary.yaml")
      instanceBaseUnitModel       <- parse("file://src/main/resources/agents/example-instances/agents.instance.yaml")
      namespaces <- Future.successful {
        Namespaces.ns ++ Map(
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
      dialectRdfModel           <- Rdf.AMF.toRdfModel(dialectBaseUnitModel, namespaces)
      vocabularyRdfModel        <- Rdf.AMF.toRdfModel(vocabularyBaseUnitUnitModel, namespaces)
      instanceRdfModel          <- Rdf.AMF.toRdfModel(instanceBaseUnitModel, namespaces)
      instanceInferenceRdfModel <- Rdf.Inference.default(instanceRdfModel, vocabularyRdfModel)
      inferredTriples           <- instanceInferenceRdfModel.difference(instanceRdfModel).wrapFuture

    } yield {
      case class Fmt(lang: String, extension: String)
      val formats = Fmt("JSON-LD", ".jsonld") :: Fmt("RDF/XML", ".xml") :: Fmt("TTL", ".ttl") :: Nil

      formats.foreach {
        case Fmt(lang, extension) =>
          Rdf.IO.write(dialectRdfModel, s"src/main/resources/agents/graphs/agents.dialect$extension", lang, dialectBaseUnitModel.id)
          Rdf.IO.write(vocabularyRdfModel, s"src/main/resources/agents/graphs/agents.vocabulary$extension", lang, vocabularyBaseUnitUnitModel.id)
          Rdf.IO.write(instanceRdfModel, s"src/main/resources/agents/graphs/agents.instance$extension", lang, instanceBaseUnitModel.id)
          Rdf.IO.write(instanceInferenceRdfModel,
                s"src/main/resources/agents/graphs-with-reasoning/agents.instance$extension",
                lang,
                instanceBaseUnitModel.id)
          Rdf.IO.write(inferredTriples,
                s"src/main/resources/agents/graphs-with-reasoning/agents.inferred.triples.instance$extension",
                lang,
                instanceBaseUnitModel.id)

      }
    }

    result onComplete {
      case Success(_) => println("Finished with success")
      case Failure(f) => println(s"Finished with failure: ${f.toString}")
    }

    Await.ready(result, Duration.Inf)
  }

  private def parse(fileUrl: String): Future[BaseUnit] = {
    println(s"Started: parse $fileUrl")
    val parser = new Aml10Parser()
    val result = parser.parseFileAsync(fileUrl).get()
    println(s"Done: parse $fileUrl")
    result.wrapFuture
  }

}
