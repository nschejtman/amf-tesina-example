package helpers
import amf.core.remote.{Amf, Aml, AsyncApi, AsyncApi20, JsonSchema, Oas, Oas20, Oas30, Payload, Raml, Raml08, Raml10, Vendor}

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
