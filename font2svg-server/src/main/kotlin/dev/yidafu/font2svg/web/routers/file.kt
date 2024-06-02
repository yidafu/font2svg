package dev.yidafu.font2svg.web.routers

import dev.yidafu.font2svg.web.beean.FileUploadResponse
import dev.yidafu.font2svg.web.beean.FontAlreadyExists
import dev.yidafu.font2svg.web.beean.Response
import dev.yidafu.font2svg.web.repository.ConfigRepository
import io.vertx.core.Vertx
import io.vertx.ext.web.Router
import io.vertx.ext.web.handler.BodyHandler
import io.vertx.kotlin.coroutines.CoroutineRouterSupport
import io.vertx.kotlin.coroutines.coAwait
import io.vertx.kotlin.coroutines.coroutineRouter
import java.nio.file.Path
import java.nio.file.Paths
import java.util.UUID
import kotlin.io.path.extension

fun buildFileUrl(filename: String): String {
  return "http://localhost:8888/asserts/fonts/$filename"
}

inline fun CoroutineRouterSupport.createFileRoute(vertx: Vertx): Router =
  Router.router(vertx).apply {
    coroutineRouter {
      val config = ConfigRepository()

      route().handler(
        BodyHandler.create()
          .setDeleteUploadedFilesOnEnd(true),
      )

      /**
       * 上传文件接口
       */
      post("/upload").coHandler { ctx ->
        val files = ctx.fileUploads()
        files.forEach { fileUpload ->
          val filename = fileUpload.fileName()
          val fs = vertx.fileSystem()
          val newFilename = UUID.randomUUID().toString() + "." + Path.of(filename).extension
          val targetPath = Paths.get(config.fontStaticAssetsPath, newFilename)
          val alreadyExist = fs.exists(targetPath.toString()).coAwait()
//            val alreadyExist = false
          if (alreadyExist) {
            ctx.json(Response.fail<FileUploadResponse>(FontAlreadyExists(filename)))
          } else {
            fs.copy(fileUpload.uploadedFileName(), targetPath.toString()).coAwait()

            val resp =
              FileUploadResponse(
                filename,
                buildFileUrl(newFilename),
                fileUpload.size(),
              )
            ctx.json(Response.success(resp))
          }
        }
      }
    }
  }
