package indyscala.aug2013.source

import dispatch._, Defaults._
import dispatch.oauth._
import com.ning.http.client.oauth.{RequestToken, ConsumerKey}
import dispatch.stream.StringsBy
import org.json4s._
import org.json4s.jackson.JsonMethods._
import com.fasterxml.jackson.core.JsonParseException
import com.typesafe.scalalogging.slf4j.Logging
import com.typesafe.config.{ConfigFactory, Config}

/**
 * Connects to the Twitter Sample API, and sends its messages to an actor.
 *
 * @author Ross A. Baker <baker@alumni.indiana.edu>
 */
object TwitterSample extends Logging {
  private val config = ConfigFactory.load().getConfig("twitter-sample")
  private val consumerKey = new ConsumerKey(config.getString("consumer-key"), config.getString("consumer-secret"))
  private val requestToken = new RequestToken(config.getString("request-token"), config.getString("request-secret"))
  private val svc = url("https://stream.twitter.com/1.1/statuses/sample.json")

  private def JValues(f: JValue => Any) = new StringsBy[Unit] {
    def onCompleted() {}
    def divider: String = """[\r\n]+"""
    def onStringBy(string: String) {
      try {
        f(parse(string))
      } catch {
        case e: JsonParseException =>
          logger.error(s"Invalid JSON: ${e.getMessage}")
      }
    }
  }

  def apply(f: JValue => Any): Future[Unit] = Http(svc.sign(consumerKey, requestToken) > JValues(f))
}
