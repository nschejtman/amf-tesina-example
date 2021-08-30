# Semantic ETL
## Requirements
* [SBT](https://www.scala-sbt.org/)
* [Pellet](https://github.com/stardog-union/pellet)
* [Maven](https://maven.apache.org/), to install Pellet

## How to install Pellet
```bash
$ git clone git@github.com:stardog-union/pellet.git
$ cd pellet
$ mvn install
```

At this time the Pellet version is 2.4.0-SNAPSHOT. Repo revision is [4c7d16b](https://github.com/stardog-union/pellet/commit/4c7d16bd1811ec04117fa4cd96ed592c6cfa956b)

## Test
```bash
$ sbt test
```