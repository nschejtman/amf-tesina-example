package test.cases.security

import com.hp.hpl.jena.rdf.model.Model
import org.scalatest.funsuite.AnyFunSuite
import pipeline.consumer.{AskConsumer, Consumer, SelectConsumer}
import pipeline.transformer.{ConstructQueryTransformer, PelletInferenceTransformer, Transformer}
import utils.Conversions.RichFloat
import utils.jena.Lang.Ttl
import utils.jena.Query

trait SecurityCaseStudyTest extends AnyFunSuite {
  val baseDir = "jena-2/src/test/resources/test/cases/security"
  val transformations: Seq[Transformer] =
    Seq(
        PelletInferenceTransformer(s"file://$baseDir/Security.ontology.ttl", Ttl),
        ConstructQueryTransformer(s"file://$baseDir/mark-insecure-operations.sparql", additive = true),
        ConstructQueryTransformer(s"file://$baseDir/mark-insecure-endpoints.sparql", additive = true),
        ConstructQueryTransformer(s"file://$baseDir/mark-insecure-api.sparql", additive = true),
        ConstructQueryTransformer(s"file://$baseDir/mark-secure.sparql", additive = true)
    )

  val APISecurityCoverageConsumer: Consumer[Float] = (model: Model) => {
    val resultSet = Query.select(model, s"file://$baseDir/security-coverage.sparql")
    val solution  = resultSet.next()
    val sensitive = solution.getLiteral("secureElementCount").getFloat
    val total     = solution.getLiteral("securableElementCount").getFloat
    (sensitive / total).roundTo(2)
  }

  val isSecureAPIConsumer: AskConsumer = AskConsumer(s"file://$baseDir/is-secure.sparql")

  val listSecureElementsConsumer: SelectConsumer = SelectConsumer(s"file://$baseDir/list-secure-elements.sparql")

}
