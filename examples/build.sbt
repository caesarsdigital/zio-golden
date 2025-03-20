val scala3Version = "3.3.5"
val zioVersion    = "2.1.16"
val circeVersion  = "0.14.12"

lazy val root = project
  .in(file("."))
  .settings(
    name         := "examples",
    version      := "0.1.0-SNAPSHOT",
    scalaVersion := scala3Version,
    resolvers += "jitpack" at "https://jitpack.io",
    libraryDependencies += "io.circe"                             %% "circe-core"        % circeVersion,
    libraryDependencies += "io.circe"                             %% "circe-generic"     % circeVersion,
    libraryDependencies += "io.circe"                             %% "circe-parser"      % circeVersion,
    libraryDependencies += "com.github.caesarsdigital.zio-golden" %% "zio-golden"        % "v0.0.2"   % Test,
    libraryDependencies += "dev.zio"                              %% "zio-test-magnolia" % zioVersion % Test,
    libraryDependencies += "dev.zio"                              %% "zio-test-sbt"      % zioVersion % Test
  )
