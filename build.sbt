import Keys._

lazy val scalaV = "2.10.6"

scalaVersion in ThisBuild := scalaV

lazy val root = (project in file("."))
  .settings(
    name := "sbt-codegen",
    organization := "timo.codegen",
    version := "0.1-SNAPSHOT",
    libraryDependencies ++= Seq(


      // yaml parser
      "org.yaml"          %   "snakeyaml"         % "1.17",
      "org.scala-lang"    %   "scala-reflect"     % scalaV // for @BeanProperty

      // code formatting
      // "com.geirsson"      %%  "scalafmt"          % "0.2.1"

      // play
      // javaCore,

    ),
    sbtPlugin := true
  )
  .enablePlugins(SbtTwirl)

ScriptedPlugin.scriptedSettings

scriptedLaunchOpts := { scriptedLaunchOpts.value ++
  Seq("-Xmx1024M", "-XX:MaxPermSize=256M", "-Dplugin.version=" + version.value)
}

scriptedBufferLog := false

