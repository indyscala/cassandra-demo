package indyscala.aug2013
package api

import akka.util.Timeout
import _root_.akka.pattern.{AskTimeoutException, ask}
import akka.actor.ActorRef
import org.scalatra._
import scala.concurrent.duration._

import service._
import org.json4s.JsonAST.JValue
import org.json4s.jackson.JsonMethods._
import org.scalatra.json.{JsonOutput, JacksonJsonOutput}
import scala.concurrent.Future
import com.typesafe.scalalogging.slf4j.Logging

/**
 * @author Ross A. Baker <baker@alumni.indiana.edu>
 */
class TweetServlet(service: ActorRef) extends ScalatraServlet with FutureSupport with JacksonJsonOutput with Logging {

  protected implicit val executor = scala.concurrent.ExecutionContext.global
  protected implicit val timeout: Timeout = 3.seconds

  get("/status") {
    service ? GetCounters
  }

  get("/tweets/:id") {
    for (optJson <- service ? GetTweet(params("id").toLong)) yield optJson match {
      case Some(json) => Ok(json)
      case None => NotFound()
    }
  }

  get("/search/:term") {
    val limit = math.min(500, params.get("limit").map(_.toInt).getOrElse(20))
    service ? Search(params("term"), params.get("lang"), limit)
  }

  get("/user/:username") {
    val limit = math.min(500, params.get("limit").map(_.toInt).getOrElse(20))
    service ? GetUser(params("username"), limit)
  }

  error {
    case e: AskTimeoutException =>
      GatewayTimeout()
    case e: Exception =>
      e.printStackTrace()
      InternalServerError()
  }

  protected override def renderPipeline: RenderPipeline = ({
    case json: JValue if (request.getParameter("pretty") != null) =>
      contentType = "application/json"
      pretty(json)
  }: RenderPipeline) orElse super.renderPipeline
}
