package indyscala.aug2013.model

import org.json4s._
import org.json4s.jackson.JsonMethods._
import org.json4s.DefaultFormats

/**
 * @author Ross A. Baker <baker@alumni.indiana.edu>
 */
class Tweet private (val json: JObject)(implicit val formats: Formats) {
  def id: Long = (json \ "id").extract[Long]
  def text: String = (json \ "text").extractOrElse("")
  def username: String = (json \ "user" \ "screen_name").extract[String]
  def language: Option[String] = (json \ "lang").extractOpt[String]

  override def toString = s"Tweet(${compact(json)}})"
}

object Tweet {
  private implicit val formats = DefaultFormats

  def apply(json: JObject) = new Tweet(json)

  def unapply(json: JValue): Option[Tweet] = json match {
    case jobj: JObject if (jobj \ "id") != JNothing => Some(new Tweet(jobj))
    case _ => None
  }
}

case class IndexedTweet(tweet: Tweet, lang: Option[String], terms: Set[String], mentions: Set[String], hashTags: Set[String])

object Delete {
  def unapply(json: JValue): Boolean = json.findField(_._1 == "delete").isDefined
}
