val releasesRepo: MavenRepository = "MuleSoft releases" at "https://repository-master.mulesoft.org/nexus/content/repositories/releases"
val publicRepo: MavenRepository   = "MuleSoft public" at "https://repository.mulesoft.org/nexus/content/repositories/public/"
val mavenCentral: MavenRepository = "Maven central" at "https://repo1.maven.org/maven2/"
val mavenLocal: MavenRepository   = "Maven local" at Path.userHome.asFile.toURI.toURL + ".m2/repository"

name := "aml-tesina-example"
ThisBuild / version := "0.1"
ThisBuild / scalaVersion := "2.12.13"
ThisBuild / resolvers ++= Seq(releasesRepo, publicRepo, mavenCentral, mavenLocal)
ThisBuild / libraryDependencies += "com.github.amlorg"             %% "amf-api-contract" % "5.0.0-beta.3"
ThisBuild / libraryDependencies += "org.scalatest"                 %% "scalatest"        % "3.2.9" % "test"
ThisBuild / libraryDependencies += "com.github.galigator.openllet" % "openllet-jena"     % "2.6.5"

// Pellet
ThisBuild / libraryDependencies += "com.clarkparsia.pellet" % "pellet-distribution" % "2.4.0-SNAPSHOT"
ThisBuild / libraryDependencies += "com.hp.hpl.jena"        % "jena"                % "2.6.4"
ThisBuild / libraryDependencies += "com.hp.hpl.jena"        % "arq"                 % "2.8.8"

parallelExecution in Test := false
