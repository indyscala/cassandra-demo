package indyscala.aug2013
package service

import akka.actor.{Props, ActorRef, Actor}
import com.cybozu.labs.langdetect.{Detector, DetectorFactory}
import scala.util.{Failure, Success, Try}
import com.typesafe.scalalogging.slf4j.Logging
import model._
import akka.routing.RoundRobinRouter

/**
 * @author Ross A. Baker <baker@alumni.indiana.edu>
 */
protected[service] class LangDetector extends Actor with Logging {
  override def preStart() {
    super.preStart()
    context.actorOf(Props[TwitterLangDetector].withRouter(RoundRobinRouter(nrOfInstances = 4)))
    context.actorOf(Props[CybozuLangDetector].withRouter(RoundRobinRouter(nrOfInstances = 4)))
  }

  def receive = {
    case tweet: Tweet =>
      for (child <- context.children)
        child forward tweet
  }
}

protected[service] class TwitterLangDetector extends Actor with Logging {
  def receive = {
    case tweet: Tweet =>
      tweet.language.foreach(lang => sender ! LangDetected(tweet, lang, "Twitter"))
  }
}

protected[service] class CybozuLangDetector extends Actor with Logging {
  import CybozuLangDetector._

  def receive = {
    case tweet: Tweet =>
      val detector = CybozuLangDetector.newDetector
      detector.append(tweet.text)
      Try(detector.detect) match {
        case Success(lang) =>
          sender ! LangDetected(tweet, lang, "Cybozu")

        case Failure(e) if e.getMessage.contains("no features in text") =>
          sender ! IncrementCounter(Counter.CybozuNoFeatures)

        case Failure(e) =>
          sender! IncrementCounter(Counter.CybozuErrors)
          logger.error("Error detecting languge", e)
      }
  }
}

protected[service] object CybozuLangDetector {
  CybozuLangDetectorYakShaver.shave()

  // Going through this method guarantees that the yak comes pre-shaven
  def newDetector: Detector = DetectorFactory.create()
}
