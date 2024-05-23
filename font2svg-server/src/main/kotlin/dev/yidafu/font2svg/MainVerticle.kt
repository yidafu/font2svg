package dev.yidafu.font2svg

import io.vertx.core.AbstractVerticle
import io.vertx.core.Promise
import io.vertx.ext.web.Router

class MainVerticle : AbstractVerticle() {

  override fun start(startPromise: Promise<Void>) {

    val server = vertx
      .createHttpServer()
    val router = Router.router(vertx)

    router.route("/").handler { ctx ->
      ctx.response().end("Hello World!")
    }

    router.get("/get").handler { ctx ->
      ctx.response().end("get request!")
    }
    server.requestHandler(router)
      .listen(8888) { http ->
        if (http.succeeded()) {
          startPromise.complete()
          println("HTTP server started on port 8888")
        } else {
          startPromise.fail(http.cause());
        }
      }
  }
}
