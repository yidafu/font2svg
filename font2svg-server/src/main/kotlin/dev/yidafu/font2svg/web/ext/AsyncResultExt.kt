package dev.yidafu.font2svg.web.ext

import io.vertx.core.AsyncResult

fun <T> AsyncResult<T>.onSuccess(block: (T) -> Unit): AsyncResult<T> {
  if (succeeded()) {
    block(result())
  }
  return this
}


fun <T> AsyncResult<T>.onFail(block: (cause: Throwable) -> Unit): AsyncResult<T> {
  if (failed()) {
    block(cause())
  }
  return this
}
