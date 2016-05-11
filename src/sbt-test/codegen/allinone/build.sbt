
lazy val root = (project in file("."))
  .settings(
    scalaVersion := "2.11.8",
    libraryDependencies ++= Seq(
      "org.tpolecat" %% "doobie-contrib-h2" % "0.2.3" % "test",
      "org.specs2"   %%  "specs2-core"      % "3.6"   % "test"
    )
  )
  .enablePlugins(CodegenPlugin)