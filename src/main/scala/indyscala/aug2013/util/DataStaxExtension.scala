package indyscala.aug2013.util

import akka.actor._
import com.datastax.driver.core.{Session, Cluster}

/**
 * @author Ross A. Baker <baker@alumni.indiana.edu>
 */
class DataStaxExtensionImpl(system: ActorSystem) extends Extension {
  private val cluster: Cluster = Cluster.builder().addContactPoint("127.0.0.1").build()
  val session: Session = cluster.connect("indyscala")

  system.registerOnTermination { session.shutdown() }
}

object DataStaxExtension extends ExtensionId[DataStaxExtensionImpl] with ExtensionIdProvider {
  def createExtension(system: ExtendedActorSystem): DataStaxExtensionImpl = new DataStaxExtensionImpl(system)

  def lookup = DataStaxExtension
}
