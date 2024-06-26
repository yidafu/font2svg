package dev.yidafu.font2svg.web.routers

import com.mayakapps.kache.InMemoryKache
import com.mayakapps.kache.KacheStrategy
import dev.yidafu.font2svg.web.beean.InvalidUrl
import dev.yidafu.font2svg.web.ext.writeFileAsync
import dev.yidafu.font2svg.web.repository.ConfigRepository
import dev.yidafu.font2svg.web.service.FontService
import io.vertx.core.Vertx
import io.vertx.core.buffer.Buffer
import io.vertx.core.http.HttpHeaders
import io.vertx.core.http.impl.MimeMapping
import io.vertx.ext.web.Router
import io.vertx.ext.web.handler.FileSystemAccess
import io.vertx.ext.web.handler.StaticHandler
import io.vertx.ext.web.validation.builder.Parameters
import io.vertx.ext.web.validation.builder.ValidationHandlerBuilder
import io.vertx.json.schema.SchemaParser
import io.vertx.json.schema.SchemaRouter
import io.vertx.json.schema.SchemaRouterOptions
import io.vertx.json.schema.common.dsl.Schemas.*
import io.vertx.kotlin.coroutines.CoroutineRouterSupport
import io.vertx.kotlin.coroutines.coroutineRouter
import org.slf4j.LoggerFactory
import java.net.URLDecoder
import java.nio.file.Paths
import kotlin.io.encoding.ExperimentalEncodingApi
import kotlin.time.Duration.Companion.days

val cache =
  InMemoryKache<String, String>(200 * 1024 * 1024) {
    strategy = KacheStrategy.LRU
    expireAfterAccessDuration = 3.days
  }

val cache404 = InMemoryKache<String, Boolean>(1024 * 1024)  {
  strategy = KacheStrategy.FIFO
  expireAfterAccessDuration = 7.days
}
inline fun CoroutineRouterSupport.createAssetRoute(vertx: Vertx): Router =
  Router.router(vertx).apply {
    coroutineRouter {
      val logger = LoggerFactory.getLogger("asserts")
      val schemaRouter = SchemaRouter.create(vertx, SchemaRouterOptions())
      val schemaParser = SchemaParser.createDraft7SchemaParser(schemaRouter)
      val configRepo = ConfigRepository()

//      route()
//        .handler(CorsHandler.create()
//          .addOrigin("*")
//          .allowedMethod(HttpMethod.GET)
//          .allowedMethod(HttpMethod.POST)
//          .allowedMethod(HttpMethod.OPTIONS)
//          .allowedHeader("Access-Control-Request-Method")
//          .allowedHeader("Access-Control-Allow-Credentials")
//          .allowedHeader("Access-Control-Allow-Origin")
//          .allowedHeader("Access-Control-Allow-Headers")
//          .allowedHeader("Content-Type"))
//      route().handler(BodyHandler.create())

      route().handler { ctx ->
        ctx.response().putHeader(HttpHeaders.ACCESS_CONTROL_ALLOW_ORIGIN, "*")
        ctx.next()
      }

      route("/fonts/*").handler(StaticHandler
        .create(FileSystemAccess.ROOT, configRepo.fontStaticAssetsPath)
        .setCachingEnabled(true))

      route("/dynamic/svg/:fontFamily/:charCode.svg")
        .handler(
          (
            ValidationHandlerBuilder.create(schemaParser)
              .pathParameter(Parameters.optionalParam("fontFamily", stringSchema()))
              .pathParameter(Parameters.optionalParam("charCode", numberSchema()))
              .queryParameter(Parameters.optionalParam("fontSize", intSchema()))
              .queryParameter(Parameters.optionalParam("color", stringSchema()))
              .queryParameter(Parameters.optionalParam("lineHeight", stringSchema()))
              .queryParameter(Parameters.optionalParam("underline", booleanSchema()))
              .build()
          ),
        ).coHandler { ctx ->
          val fontFamily = ctx.pathParam("fontFamily")
          val charCode = ctx.pathParam("charCode").toLong()
          val fontSize: Int = ctx.queryParam("fontSize").let { if (it.isEmpty()) 16 else it[0].toInt() }
          val color: String = URLDecoder.decode(ctx.queryParam("color").let { if (it.isEmpty()) "currentColor" else it[0] }, "utf-8")
          val underline: Boolean = ctx.queryParam("underline")?.let { arr -> arr.any { s -> s == "true" } } ?: false

          // in memory cache for performance
          val key = "$fontFamily-$charCode-$fontSize-$color-$underline"
          if (cache404.getIfAvailable(key) != null) {
            ctx.response().statusCode = 404
            ctx.response().end()
            return@coHandler
          }
          cache.getIfAvailable(key)?.let { svg ->
            ctx.response().putHeader(HttpHeaders.CONTENT_TYPE, MimeMapping.getMimeTypeForExtension("svg"))
            ctx.response().end(svg)
            return@coHandler
          }
          val service = FontService()

          service.getGlyph(fontFamily, charCode, fontSize, color, underline)?.let { svg ->
            ctx.response().putHeader(HttpHeaders.CONTENT_TYPE, MimeMapping.getMimeTypeForExtension("svg"))
            cache.put(key, svg)
            ctx.response().end(svg)
          } ?: run {
            cache404.put(key, true)
            ctx.response().statusCode = 404
            ctx.response().end()
          }
        }
      /**
       * /svg/:name/:.charCode.svg
       */
      route("/svg/*")
        .handler(StaticHandler.create(FileSystemAccess.ROOT, configRepo.svgStaticAssetsPath))
        // 本地资源不存在，从数据库获取
        .coHandler { ctx ->
          val path = ctx.normalizedPath()
          cache404.getIfAvailable(path)?.let {
            ctx.response().statusCode = 404
            ctx.response().end()
            return@coHandler
          }
          val paths = ctx.normalizedPath().split("/").filter { it.isNotEmpty() }
          if (paths.size != 4) {
            ctx.json(InvalidUrl())
            return@coHandler
          }

          val fontFamily = paths[2]
          val charCode = paths[3].replace(".svg", "").toLong()
          logger.info("get static glyph $fontFamily ==> $charCode")

          val service = FontService()

          service.getGlyph(fontFamily, charCode, 16, "currentColor")?.let { svg ->
            val fs = vertx.fileSystem()
            val svgPath = Paths.get(configRepo.svgStaticAssetsPath, fontFamily, "$charCode.svg").toString()

            fs.writeFileAsync(svgPath, Buffer.buffer(svg))

            ctx.response().putHeader(HttpHeaders.CONTENT_TYPE, MimeMapping.getMimeTypeForExtension("svg"))
            ctx.response().end(svg)
          } ?: run {
            cache404.put(path, true)
            ctx.response().statusCode = 404
            ctx.response().end()
          }
        }
    }
  }
