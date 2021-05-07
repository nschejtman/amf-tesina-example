# AML tesina example
## Requirements
* sbt: [https://www.scala-sbt.org/](https://www.scala-sbt.org/)

## How to run
`sbt run`

## Agents example
* `src/main/resources/agents/` contains the Dialect and Vocabulary files for the demo agents example
* `src/main/resources/agents/examples` contains YAML files that with the aid of the Agents Dialect (`agents.dialect.yaml`) will be mapped to an RDF graph
* `src/main/resources/agents/parsed` contains the RDF graph representations for the agents dialect, vocabulary and example (mapped) files in various serialization formats
* `src/main/scala/App.scala` contains the code used to generate the parsed graph representations of the source YAML files