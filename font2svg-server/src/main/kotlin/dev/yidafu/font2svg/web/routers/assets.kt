package dev.yidafu.font2svg.web.routers

import io.vertx.core.Vertx
import io.vertx.ext.web.Router

inline fun createAssetRoute(vertx: Vertx) = Router.router(vertx).apply {
  get("/font/:name/glyph/:charCode.svg").handler {

  }
}

