package indyscala.aug2013
package service

import com.datastax.driver.core.querybuilder.QueryBuilder
import java.util.Locale
import akka.actor.{ActorRef, Actor}
import indyscala.aug2013.model.{IndexedTweet, Tweet}
import scala.collection.mutable

/**
 * @author Ross A. Baker <baker@alumni.indiana.edu>
 */
protected[service] class Indexer extends Actor {
  private val MentionRegex = """^@(\w+).*""".r
  private val HashTagRegex = """^#(\w+).*""".r

  private def clean(s: String): String = {
    s.replaceAll("""[^\p{javaJavaIdentifierPart}]""", "").toLowerCase(Locale.US)
  }

  def receive = {
    case IndexTweet(tweet, lang) =>
      // I'm not proud of this...
      var mentions = mutable.Set[String]()
      var hashTags = mutable.Set[String]()
      var terms = mutable.Set[String]()

      terms += tweet.username
      terms += "@"+tweet.username

      tweet.text.split("""\s+""").foreach{ token =>
        val cleaned = clean(token)
        if (!cleaned.isEmpty) terms += cleaned

        token match {
          case MentionRegex(username) =>
            mentions += username
            terms += "@"+username

          case HashTagRegex(hashTag) =>
            hashTags += hashTag
            terms += "#"+hashTag

          case bareWord => // handled above
        }
      }

      sender ! IndexedTweet(tweet, lang, terms.toSet, mentions.toSet, hashTags.toSet)
  }
}
