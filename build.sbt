val releasesRepo: MavenRepository = "MuleSoft releases" at "https://repository-master.mulesoft.org/nexus/content/repositories/releases"
val publicRepo: MavenRepository   = "MuleSoft public" at "https://repository.mulesoft.org/nexus/content/repositories/public/"

name := "aml-tesina-example"
ThisBuild / version := "0.1"
ThisBuild / scalaVersion := "2.12.13"
ThisBuild / resolvers ++= Seq(releasesRepo, publicRepo)
ThisBuild / libraryDependencies += "com.github.amlorg" %% "amf-client" % "4.7.4"
