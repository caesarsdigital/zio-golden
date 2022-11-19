
ThisBuild / scalaVersion := "2.13.10"
ThisBuild / version := "0.0.1-SNAPSHOT"
ThisBuild / organization := "com.caesars"
ThisBuild / organizationName := "Caesars Digital"

val zioVersion = "2.0.3"
val circeVersion = "0.14.3"

lazy val root = (project in file("./zio-golden"))
  .settings(
    name := "zio-golden",

    libraryDependencies +="org.scala-lang" % "scala-reflect" % scalaVersion.value,

    libraryDependencies += "dev.zio" %% "zio" % zioVersion,
    libraryDependencies += "dev.zio" %% "zio-test" % zioVersion,
    libraryDependencies += "dev.zio" %% "zio-test-sbt" % zioVersion,
    libraryDependencies += "dev.zio" %% "zio-test-magnolia" % zioVersion,

    libraryDependencies += "dev.zio" %% "zio-nio" % "2.0.0",
    
    libraryDependencies += "io.circe" %% "circe-core" % circeVersion,
    libraryDependencies += "io.circe" %% "circe-generic" % circeVersion,
    libraryDependencies += "io.circe" %% "circe-parser" % circeVersion,

    testFrameworks += new TestFramework("zio.test.sbt.ZTestFramework")    
  )
  

