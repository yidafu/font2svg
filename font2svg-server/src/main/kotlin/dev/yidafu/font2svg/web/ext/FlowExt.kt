package dev.yidafu.font2svg.web.ext

import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow

/**
 * https://stackoverflow.com/a/76119734
 * https://github.com/Kotlin/kotlinx.coroutines/issues/1302
 */
fun <T> Flow<T>.chunked(chunkSize: Int): Flow<List<T>> {
  val buffer = mutableListOf<T>()
  return flow {
    this@chunked.collect {
      println("chunk item")
      buffer.add(it)
      if (buffer.size == chunkSize) {
        emit(buffer.toList())
        buffer.clear()
      }
    }

    if (buffer.isNotEmpty()) {
      emit(buffer.toList())
    }
  }
}
