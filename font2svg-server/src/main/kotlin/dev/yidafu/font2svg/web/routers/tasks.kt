package dev.yidafu.font2svg.web.routers

import dev.yidafu.font2svg.web.repository.TaskRepository
import io.vertx.core.Vertx
import io.vertx.ext.web.Router


inline fun createTaskRoute(vertx: Vertx) = Router.router(Vertx.vertx()).apply {

  get("/list").handler { ctx ->
    println("enter /tasks/list")
    val taskRepo = TaskRepository()

    taskRepo.findAll().whenComplete { t, u ->
        ctx.json(t)
    }
  }

  post("/create").handler {

  }

  delete("/:id").handler {

  }

  get("/:id").handler {

  }
}
