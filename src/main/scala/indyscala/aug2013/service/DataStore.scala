package indyscala.aug2013
package service

import akka.actor.Actor
import akka.pattern.pipe
import com.datastax.driver.core.querybuilder.QueryBuilder.{eq => equal, _}
import com.datastax.driver.core._
import org.json4s.jackson.JsonMethods._
import org.json4s.JsonAST._
import scala.concurrent.Future
import org.json4s.DefaultFormats
import org.json4s.JsonAST.JArray
import scala.collection.JavaConverters._
import com.typesafe.scalalogging.slf4j.Logging
import org.json4s.JsonDSL._

import model._
import util._

/**
 * @author Ross A. Baker <baker@alumni.indiana.edu>
 */
protected[service] class DataStore extends DataStaxActor with Logging {
  import context.dispatcher
  implicit val formats = DefaultFormats

  def receive = {
    // ingestion
    case IncrementCounter(Counter(name)) => incrementCounter(name)
    case StoreTweet(tweet) => storeTweet(tweet)
    case StoreIndex(IndexedTweet(tweet, lang, terms, _, _)) => storeIndex(tweet, lang, terms)
    case SetTweetLanguage(tweetId, lang, source) => setTweetLanguage(tweetId, lang, source)
    case AddUserLanguage(username, lang) => addToUser(username, "languages", Set(lang))
    case AddUserHashTags(username, hashTags) => addToUser(username, "hashtags", hashTags)
    case AddUserMentions(username, mentions) => for (mention <- mentions) addToUser(mention, "mentions", Set(username))

    // queries
    case GetCounters => getCounters()
    case GetTweet(id) => getTweet(id)
    case Search(term, lang, limit) => search(term, lang, limit)
    case GetUser(username, limit) => getUser(username, limit)
  }

  private def incrementCounter(name: String) {
    executeAsync("UPDATE counters SET count = count + 1 WHERE name = ?", name)
      .recover { case e => logger.error("Error incrementing counter", e) }
  }

  private def storeTweet(tweet: Tweet) {
    executeAsync("INSERT INTO tweets (tweet_id, text, username) VALUES (?, ?, ?)", new java.lang.Long(tweet.id), tweet.text, tweet.username)
      .recover { case e => logger.error("Error storing tweet", e) }
  }

  private def storeIndex(tweet: Tweet, lang: Option[String], terms: Set[String]) {
    val stmt = dsSession.prepare("INSERT INTO tweet_terms (term, tweet_id, lang, username, text) VALUES (?, ?, ?, ?, ?)")
    for (term <- terms) {
      val bind = new BoundStatement(stmt).bind(term, new java.lang.Long(tweet.id), lang.getOrElse("all"), tweet.username, tweet.text)
      dsSession.executeAsync(bind)
        .recover { case e => logger.error("Error storing term index", e) }
    }
  }

  private def setTweetLanguage(tweetId: Long, lang: String, source: String) {
    // Can't figure out how to do this with QueryBuilder or PreparedStatement.  Let's
    // do it the old fashioned way. *whimper*
    val langClean = lang.replaceAll("""[^a-z]""", "")
    val sourceClean = source.replaceAll("""[^A-Za-z]""", "")
    val cql = s"UPDATE tweets SET lang['${sourceClean}'] = '${langClean}' WHERE tweet_id = ${tweetId}"
    dsSession.executeAsync(cql)
      .recover { case e => logger.error("Error setting tweet language", e) }
  }

  private def addToUser(username: String, set: String, value: Set[String]) {
    executeAsync(s"UPDATE user_profiles SET ${set} = ${set} + ? WHERE username = ?", value.asJava, username)
      .recover { case e => logger.error("Error adding ${value} to set ${set} for user ${username}", e) }
  }

  private def getCounters() {
    executeAsync(s"SELECT name, count FROM counters").map { rs =>
      val map = (for (row <- rs) yield {
        row.getString("name") -> row.getLong("count")
      }).toMap
      render(map)
    } pipeTo sender
  }

  private def getTweet(id: Long) {
    executeAsync(s"SELECT * FROM tweets WHERE tweet_id = ?", new java.lang.Long(id)).map(_.toJson).map {
      case JArray(List(tweet)) => Some(tweet)
      case JArray(List()) => None
    } pipeTo sender
  }

  private def search(term: String, lang: Option[String], limit: Int) {
    val query = select("tweet_id", "username", "text").from("tweet_terms")
      .where(equal("lang", lang.getOrElse("all"))).and(equal("term", term))
      .orderBy(desc("tweet_id")).limit(limit)
    dsSession.executeAsync(query).map(_.toJson) pipeTo sender
  }

  private def getUser(username: String, limit: Int) {
    val profileF = executeAsync("SELECT * from user_profiles WHERE username = ?", username)
    val tweetsF = dsSession.executeAsync(
      select().from("tweets").where(equal("username", username)).limit(limit)
    )
    (for {
      profile <- profileF
      tweets <- tweetsF
    } yield {
      render(Map("user" -> profile.toJson, "tweets" -> tweets.toJson))
    }) pipeTo sender
  }
}
