package indyscala.aug2013.util

import scala.collection.JavaConverters._
import com.datastax.driver.core.{DataType, ColumnDefinitions, Row, ResultSet}
import org.json4s.jackson.JsonMethods._
import org.json4s.DefaultFormats
import org.json4s.JsonAST._
import org.json4s.JsonDSL._

/**
 * @author Ross A. Baker <baker@alumni.indiana.edu>
 */
trait DataStaxImplicits extends GuavaImplicits {
  private implicit val formats = DefaultFormats

  implicit class RichResultSet(rs: ResultSet) {
    def map[A](f: Row => A): Seq[A] = rs.asScala.map(f).toSeq
    def toJson: JValue = JArray(map(_.toJson(rs.getColumnDefinitions)).toList)
  }

  implicit class RichRow(row: Row) {
    def toJson(colDefs: ColumnDefinitions): JValue = {
      JObject(colDefs.asScala.map { colDef =>
        val name = colDef.getName
        val typ = colDef.getType
        // Can't pattern match because it's not a stable identifier. >:(
        val value =
          if (typ == DataType.text())
            render(row.getString(name))
          else if (typ == DataType.bigint())
            render(BigInt(row.getLong(name)))
          else if (typ == DataType.counter())
            render(BigInt(row.getLong(name)))
          else if (typ == DataType.set(DataType.text()))
            render(row.getSet(name, classOf[String]).asScala)
          else if (typ == DataType.map(DataType.text(), DataType.text()))
            render(row.getMap(name, classOf[String], classOf[String]).asScala)
          else
            sys.error(s"Sorry, ${typ} not yet implemented")
        JField(name, value)
      }.toSeq: _*)
    }
  }
}

