package dev.yidafu.font2svg.web.ext

import io.vertx.core.buffer.Buffer
import io.vertx.core.file.FileSystem
import kotlin.coroutines.suspendCoroutine

suspend fun FileSystem.writeFileAsync(path: String, data: Buffer): Boolean = suspendCoroutine<Boolean> {coutinuation ->
  writeFile(path, data) {result ->
    result.onFail {
      coutinuation.resumeWith(Result.success(false))
    }.onSuccess { coutinuation.resumeWith(Result.success(true)) }
  }
}
