package indyscala.aug2013
package service

import akka.actor.Actor
import indyscala.aug2013.model.{Delete, Tweet}
import com.typesafe.scalalogging.slf4j.Logging
import org.json4s.JsonAST.JValue
import org.json4s.jackson.JsonMethods._

import model._

/**
 * @author Ross A. Baker <baker@alumni.indiana.edu>
 */
class JsonParser extends Actor with Logging {
  def receive = {
    case Tweet(tweet) =>
      sender ! tweet
    case Delete() =>
//      logger.debug("Delete messages not implemented.")
    case json: JValue =>
      logger.error(s"Invalid tweet: ${compact(json)}")
      sender ! IncrementCounter(Counter.JsonParseErrors)
  }
}
