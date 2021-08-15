package utils.jena
import org.apache.jena.rdf.model.{Model, ModelFactory}

import java.io.FileWriter

object IO {

  def read(fileName: String, lang: Lang): Model = {
    val model = ModelFactory.createDefaultModel()
    model.read(fileName, lang.jenaId)
    model
  }

  def write(model: Model, fileName: String, lang: Lang, base: String): Unit = model.write(new FileWriter(fileName), lang.jenaId, base)

}
