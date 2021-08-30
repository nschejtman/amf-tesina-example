package utils.jena
import org.apache.jena.reasoner.{Reasoner, ReasonerRegistry}

object Reasoners {
  def default(): Reasoner = ReasonerRegistry.getOWLReasoner
}
