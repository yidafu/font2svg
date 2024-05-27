package dev.yidafu.font2svg.web.routers

import dev.yidafu.font2svg.core.FontSvgGenerator
import dev.yidafu.font2svg.dev.yidafu.font2svg.core.SvgGlyph
import dev.yidafu.font2svg.web.beean.FontFaceNotExist
import dev.yidafu.font2svg.web.ext.toSvgGlyph
import dev.yidafu.font2svg.web.ext.writeFileAsync
import dev.yidafu.font2svg.web.repository.ConfigRepository
import dev.yidafu.font2svg.web.repository.FontFaceRepository
import dev.yidafu.font2svg.web.repository.FontGlyphRepository
import io.vertx.core.Vertx
import io.vertx.core.buffer.Buffer
import io.vertx.core.http.HttpHeaders
import io.vertx.core.http.HttpMethod
import io.vertx.core.http.impl.MimeMapping
import io.vertx.ext.web.Router
import io.vertx.ext.web.handler.BodyHandler
import io.vertx.ext.web.handler.CorsHandler
import io.vertx.ext.web.handler.FileSystemAccess
import io.vertx.ext.web.handler.StaticHandler
import io.vertx.ext.web.validation.builder.Parameters
import io.vertx.ext.web.validation.builder.ValidationHandlerBuilder
import io.vertx.json.schema.SchemaParser
import io.vertx.json.schema.SchemaRouter
import io.vertx.json.schema.SchemaRouterOptions
import io.vertx.json.schema.common.dsl.Schemas
import io.vertx.json.schema.common.dsl.Schemas.*
import io.vertx.kotlin.coroutines.CoroutineRouterSupport
import io.vertx.kotlin.coroutines.coroutineRouter
import java.nio.file.Paths


inline fun CoroutineRouterSupport.createAssetRoute(vertx: Vertx): Router =
  Router.router(vertx).apply {
    coroutineRouter {

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

      route("/fonts/*").handler(StaticHandler.create(FileSystemAccess.ROOT, configRepo.fontStaticAssetsPath))

      route("/dynamic/svg/:fontFamily/:charCode.svg")
        .handler((ValidationHandlerBuilder.create(schemaParser)
            .pathParameter(Parameters.optionalParam("fontFamily", stringSchema()))
            .pathParameter(Parameters.optionalParam("charCode", numberSchema()))
            .queryParameter(Parameters.optionalParam("fontSize", intSchema()))
            .queryParameter(Parameters.optionalParam("color", stringSchema()))
            .queryParameter(Parameters.optionalParam("lineHeight", stringSchema()))
            .build()
        )).coHandler {ctx ->
          val fontFamily = ctx.pathParam("fontFamily")
          val charCode = ctx.pathParam("charCode").toLong()
          val fontSize: Int = ctx.queryParam("fontSize").let { if (it.isEmpty()) 16 else it[0].toInt() }
          val color: String = ctx.queryParam("color").let { if (it.isEmpty()) "#000000" else it[0] }

          val faceRepo = FontFaceRepository()

          val fontFace = faceRepo.getByName(fontFamily)
          if (fontFace == null) {
            ctx.json(FontFaceNotExist())
            return@coHandler
          }
          val glyphRepo = FontGlyphRepository()
          val glyph = glyphRepo.getByFontFaceAndCharCode(fontFace.id!!, charCode)
          if (glyph != null) {
            val svgGlyph = glyph.toSvgGlyph()
            val svgContent = FontSvgGenerator.glyphToSvgString(svgGlyph, fontSize, color)

            ctx.response().putHeader(HttpHeaders.CONTENT_TYPE, MimeMapping.getMimeTypeForExtension("svg"))
            ctx.response().end(svgContent)
          } else {
            ctx.response().statusCode = 404
            ctx.response().end("404")
          }
        }
      /**
       * /svg/:name/:.charCode.svg
       */
      route("/svg/*")
        .handler(StaticHandler.create(FileSystemAccess.ROOT, configRepo.svgStaticAssetsPath))
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

          val fontFace = faceRepo.getByName(name)
          if (fontFace == null) {
            ctx.json(FontFaceNotExist())
            return@coHandler
          }
          val glyphRepo = FontGlyphRepository()
          val glyph = glyphRepo.getByFontFaceAndCharCode(fontFace.id!!, charCode)
          if (glyph != null) {
            val fs = vertx.fileSystem()
            val svg = FontSvgGenerator.glyphToSvgString(glyph.toSvgGlyph(), 16, "currentColor")

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

