package indyscala.aug2013.api

import java.net.InetSocketAddress
import org.eclipse.jetty.server.Server
import org.eclipse.jetty.servlet.ServletContextHandler
import org.scalatra.servlet.ScalatraListener

/**
 * @author Ross A. Baker <baker@alumni.indiana.edu>
 */
class JettyServer(
  socketAddress: InetSocketAddress = new InetSocketAddress(8080),
  resourceBase: String = "src/main/webapp"
) {
  private[this] val server = new Server(socketAddress)
  val context = new ServletContextHandler(ServletContextHandler.SESSIONS)
  context.setContextPath("/")
  context.addEventListener(new ScalatraListener)
  context.setResourceBase(resourceBase)
  server.setHandler(context)

  def start(): this.type = {
    server.start()
    this
  }

  def stop(): this.type = {
    server.stop()
    this
  }

  def join(): this.type = {
    server.join
    this
  }
}
