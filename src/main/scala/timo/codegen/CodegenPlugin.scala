package timo.codegen

import sbt._
import Keys._

object CodegenPlugin extends AutoPlugin {

  object autoImport {

    lazy val genDbCode = TaskKey[Seq[File]]("generate-db-code", "generates the database layer")

    lazy val genBoundsCode = TaskKey[Seq[File]]("generate-bounds-code", "generates the bounds")

  }

  import autoImport._

  override def requires = sbt.plugins.JvmPlugin

  override def trigger = allRequirements

  override lazy val projectSettings =
    inConfig(Compile)(codegenSettings) ++
    inConfig(Test)(codegenSettings) ++
    dependencySettings
    /* defaultSettings ++
    positionSettings ++ */

  def codegenSettings: Seq[Setting[_]] = Seq(
    // db layer related
    includeFilter in genDbCode := "*.yml",
    excludeFilter in genDbCode := HiddenFileFilter,
    sourceDirectories in genDbCode := Seq(sourceDirectory.value / "db"),
    sources in genDbCode <<= Defaults.collectFiles(
      sourceDirectories in genDbCode,
      includeFilter in genDbCode,
      excludeFilter in genDbCode
    ),
    watchSources in Defaults.ConfigGlobal <++= sources in genDbCode,
    target in genDbCode := crossTarget.value / "gen-db" / Defaults.nameForSrc(configuration.value.name),
    genDbCode := generateDbCodeTask.value,
    sourceGenerators <+= genDbCode,
    managedSourceDirectories <+= target in genDbCode,
    // bounds layer related
    includeFilter in genBoundsCode := "*.yml",
    excludeFilter in genBoundsCode := HiddenFileFilter,
    sourceDirectories in genBoundsCode := Seq(sourceDirectory.value / "bounds"),
    sources in genBoundsCode <<= Defaults.collectFiles(
      sourceDirectories in genBoundsCode,
      includeFilter in genBoundsCode,
      excludeFilter in genBoundsCode
    ),
    watchSources in Defaults.ConfigGlobal <++= sources in genBoundsCode,
    target in genBoundsCode := crossTarget.value / "gen-bounds" / Defaults.nameForSrc(configuration.value.name),
    genBoundsCode := generateBoundsCodeTask.value,
    sourceGenerators <+= genBoundsCode,
    managedSourceDirectories <+= target in genBoundsCode
  )

  def dependencySettings: Seq[Setting[_]] = Seq(
    libraryDependencies ++= Seq(
      "svalidate" %% "svalidate" % "0.1",
      "org.tpolecat" %% "doobie-core" % "0.2.3"
    ),
    resolvers += Resolver.url("my-test-repo", url("https://dl.bintray.com/timo-schmid/maven/"))(Resolver.ivyStylePatterns)//Resolver.bintrayRepo("timo-schmid", "maven")
  )

  def generateDbCodeTask = Def.task {
    val srcDirs = (sourceDirectories in genDbCode).value
    val inclFilter = (includeFilter in genDbCode).value
    val exclFilter = (excludeFilter in genDbCode).value
    val tgtDir = (target in genDbCode).value
    val log = streams.value.log
    log.info(s"Loading db specs from: $srcDirs")
    val srcFiles = collectYmlFiles(
      srcDirs,
      inclFilter,
      exclFilter
    )
    createDbSources(srcFiles, tgtDir, log)
  }

  def generateBoundsCodeTask = Def.task {
    val srcDirs = (sourceDirectories in genBoundsCode).value
    val inclFilter = (includeFilter in genBoundsCode).value
    val exclFilter = (excludeFilter in genBoundsCode).value
    val tgtDir = (target in genBoundsCode).value
    val log = streams.value.log
    log.info(s"Loading bound specs from: $srcDirs")
    val srcFiles = collectYmlFiles(
      srcDirs,
      inclFilter,
      exclFilter
    )
    createBoundsSources(srcFiles, tgtDir, log)
  }

  private def collectYmlFiles(sourceDirectories: Seq[File], includeFilter: FileFilter, excludeFilter: FileFilter): Seq[File] = {
    sourceDirectories flatMap { sourceDirectory =>
      (sourceDirectory ** includeFilter).get flatMap { file =>
        val ext = file.name.split('.').last
        if (!excludeFilter.accept(file) && ext.equals("yml"))
          Some(file)
        else
          None
      }
    }
  }

  private def createDbSources(srcFiles: Seq[File], dstDir: File, log: Logger): Seq[File] = {
    srcFiles flatMap { srcFile =>
      val entity = YamlParser.parseYamlEntity(srcFile.getAbsolutePath)
      Seq(
        write(dstDir, Generator.toCaseClassFile(entity), log),
        write(dstDir, Generator.toIoFile(entity), log),
        write(dstDir, Generator.toRepoFile(entity), log),
        write(dstDir, Generator.toFutureRepoFile(entity), log)
      )
    }
  }

  private def createBoundsSources(srcFiles: Seq[File], dstDir: File, log: Logger): Seq[File] = {
    srcFiles flatMap { srcFile =>
      val bounds = YamlParser.parseYamlBounds(srcFile.getAbsolutePath)
      Seq(
        write(dstDir, Generator.toBoundsFile(bounds), log),
        write(dstDir, Generator.toFutureBoundsFile(bounds), log),
        write(dstDir, Generator.toTaskBoundsFile(bounds), log)
      )
    }
  }

  private def write(targetDir: File, fileData: (String, String), log: Logger): File = {
    val f = targetDir / fileData._1
    IO.write(f, fileData._2)
    log.info(s"Created: ${f.getAbsolutePath}")
    // uncomment to show the generated code
    log.debug(fileData._2)
    f
  }

}

