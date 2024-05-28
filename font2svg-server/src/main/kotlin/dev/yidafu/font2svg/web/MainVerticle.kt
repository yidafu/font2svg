package dev.yidafu.font2svg.web

import dev.yidafu.font2svg.web.beean.ErrorResponse
import dev.yidafu.font2svg.web.config.Font2SvgConfig
import dev.yidafu.font2svg.web.repository.ConfigRepository
import dev.yidafu.font2svg.web.repository.FontFaceRepository
import dev.yidafu.font2svg.web.repository.FontGlyphRepository
import dev.yidafu.font2svg.web.repository.TaskRepository
import dev.yidafu.font2svg.web.routers.createAssetRoute
import dev.yidafu.font2svg.web.routers.createFileRoute
import dev.yidafu.font2svg.web.routers.createFontRoute
import dev.yidafu.font2svg.web.routers.createTaskRoute
import dev.yidafu.font2svg.web.service.FontService
import io.vertx.config.ConfigRetriever
import io.vertx.config.ConfigRetrieverOptions
import io.vertx.config.ConfigStoreOptions
import io.vertx.core.json.JsonObject
import io.vertx.ext.web.Router
import io.vertx.ext.web.RoutingContext
import io.vertx.ext.web.validation.BadRequestException
import io.vertx.ext.web.validation.BodyProcessorException
import io.vertx.ext.web.validation.ParameterProcessorException
import io.vertx.ext.web.validation.RequestPredicateException
import io.vertx.kotlin.coroutines.CoroutineRouterSupport
import io.vertx.kotlin.coroutines.CoroutineVerticle
import io.vertx.kotlin.coroutines.coAwait
import jakarta.persistence.EntityManagerFactory
import jakarta.persistence.Persistence
import org.hibernate.reactive.stage.Stage
import org.koin.core.context.startKoin
import org.koin.dsl.module


class MainVerticle : CoroutineVerticle(), CoroutineRouterSupport {

  private lateinit var emf: EntityManagerFactory

  override suspend fun start() {

    val retriever =  ConfigRetriever.create(vertx, ConfigRetrieverOptions().addStore(ConfigStoreOptions()
      .setType("file")
      .setFormat("yaml")
      .setOptional(true)
      .setConfig(JsonObject().put("path", "font2svg.yaml"))))
    val configObj = retriever.config.coAwait()
    val config = Font2SvgConfig.build(configObj)

    val appModule = module {
      single<Stage.SessionFactory> {
        emf.unwrap(Stage.SessionFactory::class.java)
      }
      single { ConfigRepository() }
      single { TaskRepository() }
      single { FontFaceRepository() }
      single { FontGlyphRepository() }
      single { FontService() }


      single<Font2SvgConfig> { config }
    }

    startKoin {
      modules(listOf(appModule))
    }

    val server = vertx
      .createHttpServer()
    val router = Router.router(vertx)


    val props = emptyMap<String, String>()

    vertx.executeBlocking { promise ->
      emf = Persistence.createEntityManagerFactory("font2svg", props)

      promise.complete(emf.createEntityManager().createNativeQuery("SELECT 'Font2svg Ready!'").singleResult)
    }




    router.route("/assets/*").subRouter(createAssetRoute(vertx))
    router.route("/fonts/*").subRouter(createFontRoute(vertx))
    router.route("/tasks/*").subRouter(createTaskRoute(vertx))
    router.route("/files/*").subRouter(createFileRoute(vertx))

    router.get("/get").handler { ctx ->
      ctx.response().end("get request!")
    }

    router.route("/").handler { ctx ->
        ctx.response().end("Holle Font2svg")
    }
    router.errorHandler(
      400
    ) { ctx: RoutingContext ->
      val exception = ctx.failure()
      if (exception is BadRequestException) {
        if (exception is ParameterProcessorException) {
          // Something went wrong while parsing/validating a parameter
          ctx.json(ErrorResponse(400, exception.message ?: "parameter error"))
        } else if (exception is BodyProcessorException) {
          ctx.json(ErrorResponse(400, exception.message ?: "validation error"))
        } else if (ctx.failure() is RequestPredicateException) {
          // A request predicate is unsatisfied
          ctx.json(ErrorResponse(400, exception.message ?: "request predication exception"))
        }
      }
    }
    server.requestHandler(router)
      .listen(config.serverPort)
      { http ->
        if (http.succeeded()) {
          println("HTTP server started on port 8888")
        }
      }

  }

  override suspend fun stop() {
    emf.close()
  }
}

