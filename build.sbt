val scala3Version = "3.8.3"

lazy val root = project
  .in(file("."))
  .settings(
    name := "millBC",
    version := "0.1.0-SNAPSHOT",

    scalaVersion := scala3Version,

    libraryDependencies += "org.scalactic" %% "scalactic" % "3.2.14",
    libraryDependencies += "org.scalatest" %% "scalatest" % "3.2.14" % "test",
    libraryDependencies += "org.scala-lang.modules" %% "scala-swing" % "3.0.0",

    // Print test output immediately and show full-duration + detailed failures.
    Test / logBuffered := false,
    Test / testOptions += Tests.Argument(TestFrameworks.ScalaTest, "-oDF"),

    // Exclude Main from coverage
    coverageExcludedFiles := ".*Main.*;.*MillGui.*;.*MillApp.*"
  )
