package indyscala.aug2013

import org.apache.log4j.BasicConfigurator
import api.JettyServer
import com.typesafe.scalalogging.slf4j.Logging

/**
 * @author Ross A. Baker <baker@alumni.indiana.edu>
 */
object Run extends App with Logging {
  val server = new JettyServer().start()
}
