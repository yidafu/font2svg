package dev.yidafu.font2svg.web.routers

import io.vertx.core.Vertx
import io.vertx.ext.web.Router

inline fun createFontRoute(vertx: Vertx) =  Router.router(vertx).apply {

  get("/list").handler {

  }

  get("/all").handler {
    it.response().end("font all")
  }

  get("/:id").handler {
  it.response().end("font " + it.request().getParam("id"))
  }

  get("/:id/list").handler {

  }
}
