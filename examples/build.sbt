val scala3Version = "3.3.3"
val zioVersion = "2.0.11"
val circeVersion = "0.14.3"

lazy val root = project
  .in(file("."))
  .settings(
    name := "examples",
    version := "0.1.0-SNAPSHOT",
    scalaVersion := scala3Version,
    resolvers += "jitpack" at "https://jitpack.io",
    libraryDependencies += "io.circe" %% "circe-core" % circeVersion,
    libraryDependencies += "io.circe" %% "circe-generic" % circeVersion,
    libraryDependencies += "io.circe" %% "circe-parser" % circeVersion,
    libraryDependencies += "com.github.caesarsdigital.zio-golden" %% "zio-golden" % "v0.0.1" % Test,
    libraryDependencies += "dev.zio" %% "zio-test-magnolia" % zioVersion % Test,
    libraryDependencies += "dev.zio" %% "zio-test-sbt" % zioVersion % Test
  )
