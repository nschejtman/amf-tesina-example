package utils.jena
import openllet.core.OpenlletOptions
import openllet.jena.PelletReasonerFactory
import org.apache.jena.reasoner.{Reasoner, ReasonerRegistry}

object Reasoners {
  OpenlletOptions.IGNORE_UNSUPPORTED_AXIOMS = false
  OpenlletOptions.SILENT_UNDEFINED_ENTITY_HANDLING = false

  def default(): Reasoner = JenaLock.synchronized { ReasonerRegistry.getOWLReasoner }

  def pellet(): Reasoner = JenaLock.synchronized { PelletReasonerFactory.theInstance().create() }

}
