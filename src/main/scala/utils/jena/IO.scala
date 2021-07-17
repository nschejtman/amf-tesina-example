package utils.jena
import com.typesafe.scalalogging.Logger
import org.apache.jena.rdf.model.{Model, ModelFactory}

import java.io.FileWriter

object IO {

  def read(fileName: String, lang: Lang)(implicit logger: Logger): Model = {
    logger.debug(s"Started: read $fileName")
    val model = ModelFactory.createDefaultModel()
    model.read(fileName, lang.jenaId)
    logger.debug(s"Done: read $fileName")
    model
  }

  def write(model: Model, fileName: String, lang: Lang, base: String)(implicit logger: Logger): Unit = {
    logger.debug(s"Started: write $fileName")
    model.write(new FileWriter(fileName), lang.jenaId, base)
    logger.debug(s"Done: write $fileName")
  }

}
