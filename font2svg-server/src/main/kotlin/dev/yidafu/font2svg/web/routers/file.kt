package dev.yidafu.font2svg.web.routers

import dev.yidafu.font2svg.web.beean.FileUploadResponse
import dev.yidafu.font2svg.web.beean.FontAlreadyExists
import dev.yidafu.font2svg.web.repository.ConfigRepository
import io.vertx.core.Vertx
import io.vertx.ext.web.Router
import io.vertx.ext.web.handler.BodyHandler
import io.vertx.kotlin.coroutines.CoroutineRouterSupport
import io.vertx.kotlin.coroutines.coAwait
import io.vertx.kotlin.coroutines.coroutineRouter
import io.vertx.kotlin.coroutines.vertxFuture
import java.nio.file.Paths

fun buildFileUrl(filename: String): String {
  return "http://localhost:8888/assets/fonts/$filename"
}

inline fun CoroutineRouterSupport.createFileRoute(vertx: Vertx): Router =
  Router.router(vertx).apply {
    coroutineRouter {
      val config = ConfigRepository()

      route().handler(
        BodyHandler.create()
          .setDeleteUploadedFilesOnEnd(true)
      )
      /**
       * 上传文件接口
       */
      post("/upload").coHandler { ctx ->
          val files = ctx.fileUploads()
          files.forEach { fileUpload ->
            val filename = fileUpload.fileName()
            val fs = vertx.fileSystem()
            val targetPath = Paths.get(config.fontStaticAssetsPath, filename)
            val alreadyExist = fs.exists(targetPath.toString()).coAwait()
            if (alreadyExist) {
              ctx.json(FontAlreadyExists(filename))
            } else {
              fs.copy(fileUpload.uploadedFileName(), Paths.get(config.fontStaticAssetsPath, filename).toString()).coAwait()

              val resp = FileUploadResponse(
                filename,
                buildFileUrl(filename),
                fileUpload.size(),
              )
              ctx.json(resp)
            }

          }

      }
    }

  }
