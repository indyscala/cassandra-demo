package indyscala.aug2013
package service

import model._
import org.json4s.JsonAST.JValue

case class ParseTweet(json: JValue)
case class LangDetected(tweet: Tweet, lang: String, source: String)
case class IndexTweet(tweet: Tweet, lang: Option[String])

sealed trait DataStoreCommand
case class IncrementCounter(counter: Counter) extends DataStoreCommand
case class StoreTweet(tweet: Tweet) extends DataStoreCommand
case class StoreIndex(indexedTweet: IndexedTweet) extends DataStoreCommand
case class SetTweetLanguage(tweetId: Long, lang: String, source: String) extends DataStoreCommand
case class AddUserLanguage(username: String, lang: String) extends DataStoreCommand
case class AddUserMentions(username: String, mentions: Set[String]) extends DataStoreCommand
case class AddUserHashTags(username: String, hashTags: Set[String]) extends DataStoreCommand

case object GetCounters extends DataStoreCommand
case class GetTweet(tweetId: Long) extends DataStoreCommand
case class Search(term: String, lang: Option[String], limit: Int) extends DataStoreCommand
case class GetUser(username: String, limit: Int) extends DataStoreCommand


