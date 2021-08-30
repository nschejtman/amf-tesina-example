package test.cases.pii
import com.hp.hpl.jena.rdf.model.Model
import org.scalatest.funsuite.AnyFunSuite
import pipeline.consumer.{AskConsumer, Consumer, SelectConsumer}
import pipeline.transformer.OWLInferenceTransformer
import utils.jena.Lang.Ttl
import utils.jena.{Query, Reasoners}

trait PIICaseStudyTest extends AnyFunSuite {
  val baseDir = "jena-2/src/test/resources/test/cases/pii"
  val transformations: Seq[OWLInferenceTransformer] =
    OWLInferenceTransformer(s"file://$baseDir/PII.ontology.ttl", Ttl, Reasoners.pellet()) :: Nil

  val APIexposureConsumer: Consumer[Float] = (model: Model) => {
    val resultSet = Query.select(model, s"file://$baseDir/pii-sensitive-exposure.sparql")
    val solution  = resultSet.next()
    val sensitive = solution.getLiteral("sensitiveElementCount").getFloat
    val total     = solution.getLiteral("domainElementCount").getFloat
    sensitive / total
  }

  val isSensitiveAPIConsumer: AskConsumer = AskConsumer(s"file://$baseDir/is-pii-sensitive.sparql")

  val listSensitiveElementsConsumer: SelectConsumer = SelectConsumer(s"file://$baseDir/list-pii-sensitive.sparql")

}
