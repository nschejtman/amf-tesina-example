package test.cases.documentation
import org.apache.jena.rdf.model.Model
import pipeline.consumer.{AskConsumer, SelectConsumer, SyncConsumer}
import pipeline.inference.{OWLInferenceStep, SyncInferencePipeline}
import utils.jena.Lang.Ttl
import utils.jena.Query

object DocumentationCase {
  val baseDir                         = "src/test/resources/test/cases/documentation"
  val pipeline: SyncInferencePipeline = SyncInferencePipeline(OWLInferenceStep(s"file://$baseDir/Documentation.ontology.ttl", Ttl) :: Nil)

  val coverage: SyncConsumer[Float] = (model: Model) => {
    val resultSet  = Query.select(model, s"file://$baseDir/documentation-coverage.sparql")
    val solution   = resultSet.next()
    val documented = solution.getLiteral("documented").getFloat
    val total      = solution.getLiteral("total").getFloat
    documented / total
  }

  val isFullyDocumented: AskConsumer = AskConsumer(s"file://$baseDir/is-fully-documented.sparql")

  val listUndocumented: SelectConsumer = SelectConsumer(s"file://$baseDir/list-undocumented.sparql")
}
