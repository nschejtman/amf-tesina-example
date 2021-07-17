package utils.jena

sealed abstract class Lang(val jenaId: String)

object Lang {
  case object Ttl          extends Lang("TTL")
  case object Turtle       extends Lang("TURTLE")
  case object JsonLd       extends Lang("JSON-LD")
  case object N3           extends Lang("N3")
  case object NTriple      extends Lang("N-TRIPLE")
  case object RdfXml       extends Lang("RDF/XML")
  case object RdfXmlAbbrev extends Lang("RDF/XML-ABBREV")
}
