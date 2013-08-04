package indyscala.aug2013.util

import akka.actor.Actor
import com.datastax.driver.core.{ResultSetFuture, BoundStatement}

/**
 * @author Ross A. Baker <baker@alumni.indiana.edu>
 */
trait DataStaxActor extends Actor with DataStaxImplicits {
  private val extension = DataStaxExtension(context.system)
  protected def dsSession = DataStaxExtension(context.system).session

  // TODO This is not excellent.  params order still matters, and it's not using
  // the type system to the fullest.  But better this than boilerplate.
  protected def executeAsync(cql: String, params: AnyRef*): ResultSetFuture = {
    val pstmt = dsSession.prepare(cql)
    val bound = new BoundStatement(pstmt).bind(params: _*)
    dsSession.executeAsync(bound)
  }
}
