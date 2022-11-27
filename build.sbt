import BuildHelper._

inThisBuild(
  List(
    organization     := "com.caesars",
    organizationName := "Caesars Digital",
    licenses := List(
      "MPL-2.0" -> url("https://www.mozilla.org/en-US/MPL/2.0/")
    ),
    developers := List(
      Developer(
        "alex.kooper",
        "Alex Kuprienko",
        "alex.kooper@gmail.com",
        url("https://")
      )
    )
  )
)

addCommandAlias("fmt", "all scalafmtSbt scalafmt test:scalafmt")
addCommandAlias("fix", "; all compile:scalafix test:scalafix; all scalafmtSbt scalafmtAll")
addCommandAlias("check", "; scalafmtSbtCheck; scalafmtCheckAll; compile:scalafix --check; test:scalafix --check")

addCommandAlias(
  "testJVM",
  ";zioGoldenJVM/test"
)
val zioVersion   = "2.0.3"
val circeVersion = "0.14.3"

lazy val root = project
  .in(file("."))
  .settings(
    publish / skip := true,
    unusedCompileDependenciesFilter -= moduleFilter("org.scala-js", "scalajs-library")
  )
  .aggregate(
    zioGoldenJVM
    // docs
  )

lazy val zioGolden = crossProject(JVMPlatform)
  .in(file("zio-golden"))
  .settings(stdSettings("zio-golden"))
  .settings(crossProjectSettings)
  .settings(buildInfoSettings("zio.golden"))
  .settings(
    libraryDependencies ++= Seq(
      "dev.zio"  %% "zio"               % zioVersion,
      "dev.zio"  %% "zio-test"          % zioVersion,
      "dev.zio"  %% "zio-test-sbt"      % zioVersion % Test,
      "dev.zio"  %% "zio-nio"           % "2.0.0",
      "io.circe" %% "circe-core"        % circeVersion,
      "io.circe" %% "circe-generic"     % circeVersion,
      "io.circe" %% "circe-parser"      % circeVersion,
      "dev.zio"  %% "zio-test-magnolia" % zioVersion % Test,
    )
  )
  .settings(testFrameworks += new TestFramework("zio.test.sbt.ZTestFramework"))
  .enablePlugins(BuildInfoPlugin)

lazy val zioGoldenJVM = zioGolden.jvm
  .settings(dottySettings)
  .settings(libraryDependencies += "dev.zio" %%% "zio-test-sbt" % zioVersion % Test)
  .settings(scalaReflectTestSettings)

// lazy val docs = project
//   .in(file("zio-golden-docs"))
//   .settings(stdSettings("zio-golden"))
//   .settings(
//     publish / skip := true,
//     moduleName     := "zio-golden-docs",
//     scalacOptions -= "-Yno-imports",
//     scalacOptions -= "-Xfatal-warnings",
//     ScalaUnidoc / unidoc / unidocProjectFilter := inProjects(zioGoldenJVM),
//     ScalaUnidoc / unidoc / target              := (LocalRootProject / baseDirectory).value / "website" / "static" / "api",
//     cleanFiles += (ScalaUnidoc / unidoc / target).value,
//     docusaurusCreateSite     := docusaurusCreateSite.dependsOn(Compile / unidoc).value,
//     docusaurusPublishGhpages := docusaurusPublishGhpages.dependsOn(Compile / unidoc).value
//   )
//   .dependsOn(zioGoldenJVM)
//   .enablePlugins(MdocPlugin, DocusaurusPlugin, ScalaUnidocPlugin)
