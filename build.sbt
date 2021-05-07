val releasesRepo
  : MavenRepository = "MuleSoft releases" at "https://repository-master.mulesoft.org/nexus/content/repositories/releases"

name := "aml-tesina-example"
ThisBuild / version := "0.1"
ThisBuild / scalaVersion := "2.12.13"
ThisBuild / resolvers += releasesRepo
ThisBuild / libraryDependencies += "com.github.amlorg" %% "amf-aml" % "5.1.4"
