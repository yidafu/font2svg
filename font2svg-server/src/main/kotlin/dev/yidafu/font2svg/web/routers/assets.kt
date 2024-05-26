package dev.yidafu.font2svg.web.routers

import dev.yidafu.font2svg.web.beean.FontFaceNotExist
import dev.yidafu.font2svg.web.ext.writeFileAsync
import dev.yidafu.font2svg.web.repository.ConfigRepository
import dev.yidafu.font2svg.web.repository.FontFaceRepository
import dev.yidafu.font2svg.web.repository.FontGlyphRepository
import io.vertx.core.Vertx
import io.vertx.core.buffer.Buffer
import io.vertx.core.http.HttpHeaders
import io.vertx.core.http.impl.MimeMapping
import io.vertx.core.net.impl.URIDecoder
import io.vertx.ext.web.Router
import io.vertx.ext.web.handler.BodyHandler
import io.vertx.ext.web.handler.FileSystemAccess
import io.vertx.ext.web.handler.StaticHandler
import io.vertx.json.schema.SchemaParser
import io.vertx.json.schema.SchemaRouter
import io.vertx.json.schema.SchemaRouterOptions
import io.vertx.kotlin.coroutines.CoroutineRouterSupport
import io.vertx.kotlin.coroutines.coroutineRouter
import java.nio.file.Paths


inline fun CoroutineRouterSupport.createAssetRoute(vertx: Vertx): Router =
  Router.router(vertx).apply {
    coroutineRouter {

      val schemaRouter = SchemaRouter.create(vertx, SchemaRouterOptions())
      val schemaParser = SchemaParser.createDraft7SchemaParser(schemaRouter)
      val configRepo = ConfigRepository()

      val config = ConfigRepository()
      route().handler(BodyHandler.create())

      route("/fonts/*").handler(StaticHandler.create(FileSystemAccess.ROOT, config.fontStaticAssetsPath))

      /**
       * /svg/:name/:.charCode.svg
       */
      route("/svg/*")
        .handler {ctx ->
          val path = URIDecoder.decodeURIComponent(ctx.normalizedPath(), false);
          println("Path $path")
          ctx.next()
        }
        .handler(StaticHandler.create(FileSystemAccess.ROOT, config.svgStaticAssetsPath))
        // 本地资源不存在，从数据库获取
        .coHandler { ctx ->
          val paths = ctx.normalizedPath().split("/")
          println("paths ${paths.joinToString(" | ")}")
          if (paths.size == 4) {
            ctx.json("")
            return@coHandler
          }
          val faceRepo = FontFaceRepository()
          val name = paths[3]
          val charCode = paths[4].replace(".svg", "").toLong()

          val fontFace =  faceRepo.getByName(name)
          if (fontFace == null) {
            ctx.json(FontFaceNotExist())
            return@coHandler
          }
          val glyphRepo = FontGlyphRepository()
          val glyph = glyphRepo.getByFontFaceAndCharCode(fontFace.id!!, charCode)
          if (glyph != null) {
            val fs = vertx.fileSystem()
            val svg = glyph.svgContent

            val svgPath = Paths.get(configRepo.svgStaticAssetsPath, fontFace.name, "$charCode.svg").toString()
            fs.writeFileAsync(svgPath, Buffer.buffer(svg))
            ctx.response().putHeader(HttpHeaders.CONTENT_TYPE, MimeMapping.getMimeTypeForExtension("svg"))
            ctx.response().end(svg)
          } else {
            ctx.response().statusCode = 404
            ctx.response().end("404")
          }
        }
    }
  }

