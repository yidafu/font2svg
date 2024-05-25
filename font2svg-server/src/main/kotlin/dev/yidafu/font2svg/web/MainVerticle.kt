package dev.yidafu.font2svg.web

import dev.yidafu.font2svg.web.repository.TaskRepository
import dev.yidafu.font2svg.web.routers.createAssetRoute
import dev.yidafu.font2svg.web.routers.createFontRoute
import dev.yidafu.font2svg.web.routers.createTaskRoute
import io.vertx.core.AbstractVerticle
import io.vertx.core.Promise
import io.vertx.ext.web.Router
import jakarta.persistence.EntityManagerFactory
import jakarta.persistence.Persistence
import org.hibernate.reactive.stage.Stage
import org.koin.core.context.startKoin
import org.koin.dsl.module


class MainVerticle : AbstractVerticle() {

  private lateinit var emf: EntityManagerFactory

  override fun start(startPromise: Promise<Void>) {
    val appModule = module {
      single {
        TaskRepository()
      }

      single<Stage.SessionFactory> {
        emf.unwrap(Stage.SessionFactory::class.java)
      }
    }

    val server = vertx
      .createHttpServer()
    val router = Router.router(vertx)

    val props = emptyMap<String, String>()

    vertx.executeBlocking { promise ->
      emf = Persistence.createEntityManagerFactory("font2svg", props)

      promise.complete(emf.createEntityManager().createNativeQuery("SELECT 'Font2svg Ready!'").singleResult)
    }



    startKoin {
      modules(listOf(appModule))
    }


    router.route("/assets/*").subRouter(createAssetRoute(vertx))
    router.route("/fonts/*").subRouter(createFontRoute(vertx))
    router.route("/tasks/*").subRouter(createTaskRoute(vertx))

    router.get("/get").handler { ctx ->
      ctx.response().end("get request!")
    }

    router.route("/").handler { ctx ->
        ctx.response().end("Holle Font2svg")
    }

    server.requestHandler(router)
      .listen(8888)
      { http ->
        if (http.succeeded()) {
          startPromise.complete()

          println("HTTP server started on port 8888")
        } else {
          startPromise.fail(http.cause());
        }
      }

  }
}

