package indyscala.aug2013.util

import com.google.common.util.concurrent.{FutureCallback, Futures, ListenableFuture}
import scala.concurrent.{Promise, ExecutionContext, Future}

/**
 * @author Ross A. Baker <baker@alumni.indiana.edu>
 */
trait GuavaImplicits {
  implicit def listenableFuture2ScalaFuture[A](future: ListenableFuture[A])(implicit executor: ExecutionContext): Future[A] = {
    val promise = Promise[A]
    Futures.addCallback(future, new FutureCallback[A] {
      def onSuccess(result: A) {
        promise.success(result)
      }
      def onFailure(t: Throwable) {
        promise.failure(t)
      }
    })
    promise.future
  }
}
