package utils.jena
import org.apache.jena.query.ResultSet
import org.apache.jena.rdf.model.Model

object Printer {
  val DIVIDER = "-----------------------------------------------------------------------------------------------------------------"

  def print(model: Model, lang: Lang): Unit = model.write(System.out, lang.jenaId)

  def print(resultSet: ResultSet): Unit = {
    val vars = resultSet.getResultVars
    var i    = 0
    println(DIVIDER)
    resultSet.forEachRemaining { result =>
      println(s"Result: $i")
      vars.forEach { variable =>
        val value = result.get(variable)
        println(s"$variable: $value")
      }
      i += 1
      println(DIVIDER)
    }
  }

}
