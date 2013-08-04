package indyscala.aug2013
package service

import akka.actor.{Props, ActorRef, Actor}
import com.typesafe.scalalogging.slf4j.Logging
import org.json4s.JsonAST.JValue

import model._
import akka.routing.RoundRobinRouter
import scala.reflect.ClassTag

/**
 * @author Ross A. Baker <baker@alumni.indiana.edu>
 */
class TweetService extends Actor with Logging {
  private var jsonParser: ActorRef = _
  private var dataStore: ActorRef = _
  private var langDetector: ActorRef = _
  private var indexer: ActorRef = _

  override def preStart() {
    super.preStart()
    jsonParser = startActorPool[JsonParser]()
    dataStore = startActorPool[DataStore]()
    indexer = startActorPool[Indexer]()
    langDetector = startActorPool[LangDetector]()
  }

  private def startActorPool[A <: Actor](count: Int = 4)(implicit tag: ClassTag[A]): ActorRef = {
    context.actorOf(Props[A].withRouter(RoundRobinRouter(nrOfInstances = count)))
  }

  def receive = {
    case ParseTweet(json) => jsonParser ! json
    case tweet: Tweet => onTweet(tweet)
    case LangDetected(tweet, lang, source) => onLangDetected(tweet, lang, source)
    case indexedTweet: IndexedTweet => onIndexedTweet(indexedTweet)
    case cmd: DataStoreCommand => dataStore forward cmd
  }

  private def onTweet(tweet: Tweet) {
    dataStore ! IncrementCounter(Counter.Total)
    dataStore ! StoreTweet(tweet)
    langDetector ! tweet
    indexer ! IndexTweet(tweet, None)
  }

  private def onLangDetected(tweet: Tweet, lang: String, source: String) {
    dataStore ! SetTweetLanguage(tweet.id, lang, source)
    dataStore ! AddUserLanguage(tweet.username, lang)
    indexer ! IndexTweet(tweet, Some(lang))
  }

  private def onIndexedTweet(iTweet: IndexedTweet) {
    dataStore ! StoreIndex(iTweet)
    dataStore ! AddUserMentions(iTweet.tweet.username, iTweet.mentions)
    dataStore ! AddUserHashTags(iTweet.tweet.username, iTweet.hashTags)
  }
}
