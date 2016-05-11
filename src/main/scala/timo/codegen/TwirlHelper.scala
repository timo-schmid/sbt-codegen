package timo.codegen

object TwirlHelper {

  def vars(fields: Seq[FieldDef]): String =
    fields map { _.name } mkString ", "

  def types(fields: Seq[FieldDef]): String =
    fields map { _.tpe } map { toScalaType } mkString ", "

  def fld(field: FieldDef): String =
    s"${field.name}: ${toScalaType(field.tpe)}"

  def fields(fields: Seq[FieldDef], delim: String = ", "): String =
    fields.map { fld } mkString delim

  def sqlQueryFields(fields: Seq[FieldDef], prefix: String = "", delim: String = ", "): String =
    fields.map { field =>
      s"${field.name} = $${$prefix${field.name}}"
    } mkString delim

  def varNames(fields: Seq[FieldDef]): String =
    fields.map(_.name).mkString(", ")

  def ioFinders(entity: Entity): String =
    (entity.keys ++ entity.fields).map { ioFinder(entity, _) } mkString "\n\n"

  def ioFinder(entity: Entity, field: FieldDef): String =
    txt.IOFinder.render(entity, field).body

  def repoFinders(entity: Entity): String =
    (entity.keys ++ entity.fields).map { repoFinder(entity, _) } mkString "\n\n"

  def repoFinder(entity: Entity, field: FieldDef): String =
    txt.RepoFinder.render(entity, field).body

  def futureRepoFinders(entity: Entity): String =
    (entity.keys ++ entity.fields).map { futureRepoFinder(entity, _) } mkString "\n\n"

  def futureRepoFinder(entity: Entity, field: FieldDef): String =
    txt.FutureRepoFinder.render(entity, field).body

  def validations(bounds: Bounds): String =
    bounds.validations.map { validation =>
      s"""    Validation("${validation.label}", ${validation.getter}, ${validation.validator})"""
    } mkString ",\n    "

  // c

  // nice table to look this stuff up:
  // http://doctrine-orm.readthedocs.io/projects/doctrine-dbal/en/latest/reference/types.html
  // http://www.tutorialspoint.com/jdbc/jdbc-data-types.htm
  def toScalaType(tpe: String): String = tpe.toLowerCase match {
    case "boolean"    => "Boolean"
    case "tinyint"    => "Byte"
    case "smallint"   => "Int"
    case "int"        => "Int"
    case "integer"    => "Int"
    case "bigint"     => "Int"
    case "real"       => "Float"
    case "float"      => "Float"
    case "decimal"    => "Float"
    case "double"     => "Double"
    case "bigdecimal" => "BigDecimal"
    case "char"       => "Char"
    case "string"     => "String"
    case "text"       => "String"
    case "varbinary"  => "Array[Byte]"
    case "binary"     => "Array[Byte]"
    case "blob"       => "Array[Byte]"
    case "date"       => "java.util.Date"
    case "datetime"   => "java.util.Date"
    // TODO
    case "datetimez"    => ???
    case "dateinterval" => ???
    case "guid"         => ???
    // unknown
    case _              => throw new RuntimeException(s"Unknown data type: $tpe")
  }

}

