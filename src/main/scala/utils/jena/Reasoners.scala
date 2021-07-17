package utils.jena
import openllet.jena.PelletReasonerFactory
import org.apache.jena.reasoner.{Reasoner, ReasonerRegistry}

object Reasoners {
  def default(): Reasoner = ReasonerRegistry.getOWLReasoner
  def pellet(): Reasoner  = PelletReasonerFactory.theInstance().create()
}
