package timo.codegen

import timo.codegen.TwirlHelper._
// import org.scalafmt.Scalafmt
// import org.scalafmt.FormatResult.{Success, Incomplete, Failure}

trait FieldDef {
  val name: String
  val tpe: String
  def finder: String = s"findBy${name.capitalize}"
  def singleFinder: String = s"findOneBy${name.capitalize}"
}

case class EntityKey(
  name: String,
  tpe: String,
  length: Option[Int]
) extends FieldDef

case class EntityField(
  name: String,
  tpe: String,
  length: Option[Int],
  unique: Boolean
) extends FieldDef

trait PkgScoped {
  val name: String
  val pkg: Option[String]
}

case class Entity(
  name: String,
  tableName: String,
  pkg: Option[String],
  keys: Seq[EntityKey],
  fields: Seq[EntityField]
) extends PkgScoped {
  def ioName: String          = s"${name}IO"               // TODO tmake configurable
  def repoName: String        = s"${name}Repository"       // dito
  def futureRepoName: String  = s"${name}FutureRepository" // dito
  def nameWithPackage: String = pkg.map(_ + "." + name).getOrElse(name)
}

case class Validation(
  label: String,
  getter: String,
  validator: String
)

case class Bounds(
  name: String,
  pkg: Option[String],
  tpe: String,
  validations: Seq[Validation]
) extends PkgScoped {
  def futureBoundsName: String = s"Future$name" // TODO make configurable
  def taskBoundsName: String   = s"Task$name"
}

object Generator {

  /*
  def main(args: Array[String]): Unit = {
    val entity = YamlParser.parseYaml("db/user.yml")
    generate(entity, "../doobie-codegen-test/src/main/scala/")
  }

  def generate(entity: Entity, outputDir: String): Unit = {
    val (caseClassFileName, caseClassFileContent) = toCaseClassFile(entity)
    val (repoFileName,      repoFileContent)      = toRepoFile(entity)
    val (futRepoFileName,   futRepoFileContent)   = toFutureRepoFile(entity)
    val (ioFileName,        ioFileContent)        = toIoFile(entity)
    IO.write(outputDir + caseClassFileName, caseClassFileContent)
    IO.write(outputDir + repoFileName,      repoFileContent)
    IO.write(outputDir + futRepoFileName,   futRepoFileContent)
    IO.write(outputDir + ioFileName,        ioFileContent)
  }
  */

  def fullFilePath(pkgScoped: PkgScoped, fileName: String): String =
    pkgScoped
      .pkg
      .getOrElse("")
      .replaceAll("\\.", "/") + "/" + fileName

  def toCaseClassFile(entity: Entity): (String, String) = (
    fullFilePath(entity, entity.name + ".scala"),
    txt.CaseClass(entity).body
  )

  def toIoFile(entity: Entity): (String, String) = (
    fullFilePath(entity, entity.ioName + ".scala"),
    txt.IO(entity).body
  )

  def toRepoFile(entity: Entity): (String, String) = (
    fullFilePath(entity, entity.repoName + ".scala"),
    txt.Repository(entity).body
  )

  def toFutureRepoFile(entity: Entity): (String, String) = (
    fullFilePath(entity, entity.futureRepoName + ".scala"),
    txt.FutureRepository(entity).body
  )

  def toBoundsFile(bounds: Bounds): (String, String) = (
    fullFilePath(bounds, bounds.name + ".scala"),
    txt.Bounds(bounds).body
  )

  def toFutureBoundsFile(bounds: Bounds): (String, String) = (
    fullFilePath(bounds, bounds.futureBoundsName + ".scala"),
    txt.FutureBounds(bounds).body
  )

  def toTaskBoundsFile(bounds: Bounds): (String, String) = (
    fullFilePath(bounds, bounds.taskBoundsName + ".scala"),
    txt.TaskBounds(bounds).body
  )

  /*
  def format(original: String): String =
    Scalafmt.format(original) match {
      case Success(formatted)    => formatted
      case Incomplete(formatted) => formatted
      case Failure(t)            => {
        t.printStackTrace // TODO error handling
        original
      }
    }
  */

}
