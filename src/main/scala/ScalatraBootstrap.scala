import akka.routing.RoundRobinRouter
import indyscala.aug2013.api.TweetServlet
import org.scalatra.LifeCycle
import javax.servlet.ServletContext
import akka.actor.{Props, ActorSystem}
import indyscala.aug2013.service._
import indyscala.aug2013.source.TwitterSample
import scala.concurrent.duration._

/**
 * @author Ross A. Baker <baker@alumni.indiana.edu>
 */
class ScalatraBootstrap extends LifeCycle {
  private var system: ActorSystem = _

  override def init(context: ServletContext) {
    super.init(context)
    system = ActorSystem()
    val service = system.actorOf(Props[TweetService].withRouter(RoundRobinRouter(nrOfInstances = 5)))
    TwitterSample { json => service ! ParseTweet(json) }
    context.mount(new TweetServlet(service), "/*")
  }

  override def destroy(context: ServletContext) {
    system.shutdown()
    system.awaitTermination(10.seconds)
    super.destroy(context)
  }
}
