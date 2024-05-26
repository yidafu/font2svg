package dev.yidafu.font2svg.web.routers

import dev.yidafu.font2svg.web.repository.ConfigRepository
import io.vertx.core.Vertx
import io.vertx.ext.web.Router
import io.vertx.ext.web.handler.FileSystemAccess
import io.vertx.ext.web.handler.StaticHandler
import io.vertx.kotlin.coroutines.CoroutineRouterSupport
import io.vertx.kotlin.coroutines.coroutineRouter


inline fun CoroutineRouterSupport.createAssetRoute(vertx: Vertx): Router =
  Router.router(vertx).apply {
    coroutineRouter {
      val config = ConfigRepository()
      route("/fonts/*").handler(StaticHandler.create(FileSystemAccess.ROOT, config.fontStaticAssetsPath))

      get("/svg/:name/glyph/:charCode.svg").handler {

      }
    }
  }

