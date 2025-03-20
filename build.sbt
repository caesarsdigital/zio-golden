inThisBuild(
  List(
    organization       := "com.caesars",
    organizationName   := "Caesars Digital",
    scalaVersion       := "2.13.16",
    crossScalaVersions := List("2.13.16", "3.3.5"),
    licenses := List(
      "MPL-2.0" -> url("https://www.mozilla.org/en-US/MPL/2.0/")
    ),
  )
)

addCommandAlias("fmt", "all scalafmtSbt scalafmt test:scalafmt")
addCommandAlias("fix", "; all compile:scalafix test:scalafix; all scalafmtSbt scalafmtAll")
addCommandAlias("check", "; scalafmtSbtCheck; scalafmtCheckAll; compile:scalafix --check; test:scalafix --check")

addCommandAlias(
  "testJVM",
  ";zioGoldenJVM/test"
)
val zioVersion   = "2.1.16"
val circeVersion = "0.14.12"

lazy val root = project
  .in(file("."))
  .settings(publish / skip := true)
  .aggregate(zioGolden)

lazy val zioGolden = project
  .in(file("zio-golden"))
  .settings(
    libraryDependencies ++= Seq(
      "dev.zio"  %% "zio"               % zioVersion,
      "dev.zio"  %% "zio-test"          % zioVersion,
      "dev.zio"  %% "zio-test-sbt"      % zioVersion % Test,
      "dev.zio"  %% "zio-nio"           % "2.0.2",
      "io.circe" %% "circe-core"        % circeVersion,
      "io.circe" %% "circe-generic"     % circeVersion,
      "io.circe" %% "circe-parser"      % circeVersion,
      "dev.zio"  %% "zio-test-magnolia" % zioVersion % Test,
    )
  )
  .settings(testFrameworks += new TestFramework("zio.test.sbt.ZTestFramework"))
  .enablePlugins(BuildInfoPlugin)
