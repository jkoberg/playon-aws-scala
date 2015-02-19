package com.playonsports.aws

import com.amazonaws.AmazonWebServiceRequest
import com.amazonaws.handlers.AsyncHandler

import scala.concurrent.Promise


object AmazonFuturesHelper {
  /**
 * All the AWS SDK methods return a java.util.concurrent.Future[V], which isn't as
 * adaptable as Scala's scala.concurrent.Future[T]. So this will transform those functions
 * by passing an anonymous AsyncHandler which completes a Scala promise/future pair.
 * @param aMethod the method to adapt
 * @tparam T the request type
 * @tparam U the result type
 */
  def adapt[T<:AmazonWebServiceRequest,U](aMethod:(T,AsyncHandler[T,U])=>java.util.concurrent.Future[U])(request:T) = {
    val p = Promise[U]
    aMethod(request, new AsyncHandler[T, U]() {
      def onSuccess(req: T, res: U) = p.success(res)
      def onError(ex: Exception) = p.failure(ex)
    })
    p.future
  }
}
