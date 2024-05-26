package dev.yidafu.font2svg.web.routers

import io.vertx.core.Vertx
import io.vertx.ext.web.Router
import io.vertx.kotlin.coroutines.CoroutineRouterSupport
import io.vertx.kotlin.coroutines.coroutineRouter
import kotlinx.coroutines.coroutineScope

inline fun CoroutineRouterSupport.createFontRoute(vertx: Vertx): Router = Router.router(vertx).apply {
  coroutineRouter {
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
}
