package utils.amf

import amf.core.remote._

object MediaType {
  val yaml   = "application/yaml"
  val json   = "application/json"
  val jsonld = "application/ld+json"

  val forVendor = Map(
      Raml       -> yaml,
      Raml08     -> yaml,
      Raml10     -> yaml,
      Oas        -> json,
      Oas20      -> json,
      Oas30      -> json,
      AsyncApi   -> yaml,
      AsyncApi20 -> yaml,
      Amf        -> jsonld,
      Payload    -> json,
      Aml        -> yaml,
      JsonSchema -> json
  )

}
