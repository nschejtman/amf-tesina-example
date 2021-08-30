package utils.jena
import com.hp.hpl.jena.reasoner.{Reasoner, ReasonerRegistry}
import org.mindswap.pellet.jena.PelletReasonerFactory

object Reasoners {

  def default(): Reasoner = ReasonerRegistry.getOWLReasoner

  def pellet(): Reasoner = PelletReasonerFactory.theInstance().create()

}
