package pipeline.transformer
import com.hp.hpl.jena.ontology.OntModelSpec
import com.hp.hpl.jena.rdf.model.{Model, ModelFactory}
import org.mindswap.pellet.jena.PelletReasonerFactory
import utils.Conversions.Url
import utils.jena._

import java.io.FileInputStream

case class PelletInferenceTransformer(ontologyUrl: String, lang: Lang) extends Transformer {

  override def transform(model: Model): Model = {
    val ontology            = ModelFactory.createOntologyModel(OntModelSpec.OWL_MEM)
    val ontologyInputStream = new FileInputStream(ontologyUrl.withProtocol(""))
    ontology.read(ontologyInputStream, null, "TTL")
    ontology.add(model);
    ModelFactory.createOntologyModel(PelletReasonerFactory.THE_SPEC, ontology)
  }
}
