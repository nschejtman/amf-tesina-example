package test.cases.documentation
import org.apache.jena.rdf.model.Model
import org.scalatest.funsuite.AnyFunSuite
import pipeline.consumer.{AskConsumer, Consumer, SelectConsumer}
import pipeline.transformer.{OWLInferenceTransformer, Transformer}
import utils.jena.Lang.Ttl
import utils.jena.Query

trait DocumentationCaseStudyTest extends AnyFunSuite {
  val baseDir = "src/test/resources/test/cases/documentation"

  val transformations: Seq[Transformer] = OWLInferenceTransformer(s"file://$baseDir/Documentation.ontology.ttl", Ttl) :: Nil

  val coverageConsumer: Consumer[Float] = (model: Model) => {
    val resultSet  = Query.select(model, s"file://$baseDir/documentation-coverage.sparql")
    val solution   = resultSet.next()
    val documented = solution.getLiteral("documented").getFloat
    val total      = solution.getLiteral("total").getFloat
    documented / total
  }

  val isFullyDocumentedConsumer: AskConsumer = AskConsumer(s"file://$baseDir/is-fully-documented.sparql")

  val listUndocumentedConsumer: SelectConsumer = SelectConsumer(s"file://$baseDir/list-undocumented.sparql")
}
