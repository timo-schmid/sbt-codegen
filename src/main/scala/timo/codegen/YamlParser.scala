package timo.codegen

import scala.beans.BeanProperty
import scala.collection.JavaConverters._
import scala.io.Source
import org.yaml.snakeyaml.Yaml
import org.yaml.snakeyaml.TypeDescription
import org.yaml.snakeyaml.constructor.Constructor
import java.util.{Map => JMap}
import java.util.{HashMap => JHashMap}

object YamlParser {

  case class YEntityKey(
    @BeanProperty var `type`: String,
    @BeanProperty var length: Int = 0
  ) {
    def this() = this(null)
  }

  case class YEntityField(
    @BeanProperty var `type`: String,
    @BeanProperty var length: Int = 0,
    @BeanProperty var unique: Boolean = false
  ) {
    def this() = this(null)
  }

  case class YValidation(
    @BeanProperty var label: String = null,
    @BeanProperty var getter: String = null,
    @BeanProperty var validator: String = null
  ) {
    def this() = this(null, null, null)
  }

  case class YBounds(
    @BeanProperty var name: String,
    @BeanProperty var `package`: String = null,
    @BeanProperty var `type`: String,
    @BeanProperty var validations: JMap[String, YValidation] = new JHashMap()
  ) {
    def this() = this(null, null, null, new JHashMap())
  }

  case class YEntity(
    @BeanProperty var name: String = null,
    @BeanProperty var tableName: String = null,
    @BeanProperty var `package`: String = null,
    @BeanProperty var keys: JMap[String, YEntityKey] = new JHashMap(),
    @BeanProperty var fields: JMap[String, YEntityField] = new JHashMap()
  ) {
    def this() = this(null, null, null, new JHashMap(), new JHashMap())
  }

  private def toScalaKey(name: String, yKey: YEntityKey): EntityKey =
    EntityKey(
      name,
      yKey.`type`,
      Option(yKey.length).filter(_ != 0)
    )

  private def toScalaField(name: String, yField: YEntityField): EntityField =
    EntityField(
      name,
      yField.`type`,
      Option(yField.length).filter(_ != 0),
      yField.unique
    )

  private def toScalaValidation(name: String, yValidation: YValidation): Validation =
    Validation(
      Option(yValidation.label).getOrElse(name),
      yValidation.getter,
      yValidation.validator
    )

  private def toScalaEntity(yEntity: YEntity): Entity =
    Entity(
      yEntity.name,
      Option(yEntity.tableName).getOrElse(yEntity.name),
      Option(yEntity.`package`),
      yEntity.keys.asScala.map { t => toScalaKey(t._1, t._2) }.toSeq,
      yEntity.fields.asScala.map { t => toScalaField(t._1, t._2) }.toSeq
    )

  private def toScalaBounds(yBounds: YBounds): Bounds =
    Bounds(
      yBounds.name,
      Option(yBounds.`package`),
      yBounds.`type`,
      yBounds.validations.asScala.map { t => toScalaValidation(t._1, t._2) }.toSeq
    )

  def parseYamlEntity(fname: String): Entity = {
    val yamlStr = Source.fromFile(fname).getLines.mkString("\n")
    val entityDesc = new TypeDescription(classOf[YEntityKey], "!entity")
    val c = new Constructor(classOf[YEntity])
    toScalaEntity(new Yaml(c).load(yamlStr).asInstanceOf[YEntity])
  }

  def parseYamlBounds(fname: String): Bounds = {
    val yamlStr = Source.fromFile(fname).getLines.mkString("\n")
    val entityDesc = new TypeDescription(classOf[YValidation], "!bound")
    val c = new Constructor(classOf[YBounds])
    toScalaBounds(new Yaml(c).load(yamlStr).asInstanceOf[YBounds])
  }

}

