val releasesRepo: MavenRepository = "MuleSoft releases" at "https://repository-master.mulesoft.org/nexus/content/repositories/releases"
val publicRepo: MavenRepository   = "MuleSoft public" at "https://repository.mulesoft.org/nexus/content/repositories/public/"
val mavenCentral: MavenRepository = "Maven central" at "https://repo1.maven.org/maven2/"
val mavenLocal: MavenRepository   = "Maven local" at Path.userHome.asFile.toURI.toURL + ".m2/repository"

name := "semantic-etl"
ThisBuild / version := "0.1"
ThisBuild / scalaVersion := "2.12.13"
ThisBuild / resolvers ++= Seq(releasesRepo, publicRepo, mavenCentral, mavenLocal)
ThisBuild / parallelExecution in Test := false
ThisBuild / libraryDependencies += "org.scalatest" %% "scalatest" % "3.2.9" % "test"

lazy val jena3 = (project in file("jena-3"))
  .settings(
      libraryDependencies += "com.github.amlorg" %% "amf-api-contract" % "5.0.0-beta.3",
      libraryDependencies += "org.apache.jena"   % "jena"              % "3.17.0" pomOnly ()
  )

lazy val jena2 = (project in file("jena-2"))
  .settings(
      libraryDependencies += "com.clarkparsia.pellet" % "pellet-distribution" % "2.4.0-SNAPSHOT",
      libraryDependencies += "org.apache.jena"        % "jena"                % "2.13.0" pomOnly ()
  )
