package dev.yidafu.font2svg.web.routers

import dev.yidafu.font2svg.web.beean.*
import dev.yidafu.font2svg.web.model.FontGlyph
import dev.yidafu.font2svg.web.repository.FontFaceRepository
import dev.yidafu.font2svg.web.repository.FontGlyphRepository
import dev.yidafu.font2svg.web.repository.TaskRepository
import io.vertx.core.Vertx
import io.vertx.ext.web.Router
import io.vertx.ext.web.handler.BodyHandler
import io.vertx.ext.web.validation.builder.Parameters
import io.vertx.ext.web.validation.builder.ValidationHandlerBuilder
import io.vertx.json.schema.SchemaParser
import io.vertx.json.schema.SchemaRouter
import io.vertx.json.schema.SchemaRouterOptions
import io.vertx.json.schema.common.dsl.Schemas.numberSchema
import io.vertx.kotlin.coroutines.CoroutineRouterSupport
import io.vertx.kotlin.coroutines.coroutineRouter

inline fun CoroutineRouterSupport.createFontRoute(vertx: Vertx): Router = Router.router(vertx).apply {
  coroutineRouter {
    route().handler(BodyHandler.create())

    val schemaRouter = SchemaRouter.create(vertx, SchemaRouterOptions())
    val schemaParser = SchemaParser.createDraft7SchemaParser(schemaRouter)


    get("/all").coHandler { ctx ->
      val fontRepo = FontFaceRepository()
      val taskRepo = TaskRepository()
      val list = fontRepo.getAll()
      val tasks = taskRepo.findAllByIdList(list.mapNotNull { it.id }.toList())
      val detailDTOList =  list.map { face ->
        val taskBelong2Face = tasks.filter { it.fontFaceId == face.id }
        FontFaceDetailDTO.from(face, taskBelong2Face)
      }
      ctx.json(Response.success(detailDTOList))
    }

    get("/:id")
      .handler(
        ValidationHandlerBuilder
          .create(schemaParser)
          .pathParameter(Parameters.param("id", numberSchema()))
          .build()
      )
      .coHandler { ctx ->
        val faceRepo = FontFaceRepository()
        val taskRepo = TaskRepository()
        val fontFaceId = ctx.pathParam("id").toLong()
        val fontFace = faceRepo.getById(fontFaceId)
        val tasks = taskRepo.getByFaceId(fontFaceId)
        if (fontFace == null) {
          ctx.json(Response.fail<FontFaceDetailDTO>(ContentNotFound()))
        } else {
          ctx.json(Response.success(FontFaceDetailDTO.from(fontFace, tasks)))
        }
      }

    get("/:id/glyph/:glyphId")
      .handler(
        ValidationHandlerBuilder
          .create(schemaParser)
          .pathParameter(Parameters.param("id", numberSchema()))
          .pathParameter(Parameters.param("glyphId", numberSchema()))
          .build()
      )
      .coHandler { ctx ->
        val fontFaceId = ctx.pathParam("id").toLong()
        val fontGlyphId = ctx.pathParam("glyphId").toLong()

        val glyphRepo = FontGlyphRepository()
        val fontRepo = FontFaceRepository()
        val face = fontRepo.getById(fontFaceId)
        if (face == null) {
          ctx.json(Response.fail<FontGlyph>(FontFaceNotExist()))
          return@coHandler
        }
        val glyph = glyphRepo.getByFontFaceAndGlyphId(fontFaceId, fontGlyphId)
        if (glyph == null) {
          ctx.json(Response.fail<FontGlyph>(ContentNotFound()))
        } else {
          ctx.json(Response.success(glyph))
        }
      }

    get("/:id/glyphs")
      .handler(
        ValidationHandlerBuilder
          .create(schemaParser)
          .pathParameter(Parameters.param("id", numberSchema()))
          .queryParameter(Parameters.optionalParam("page", numberSchema()))
          .queryParameter(Parameters.optionalParam("size", numberSchema()))
          .build()
      )
      .coHandler {ctx ->
        val fontFaceId = ctx.pathParam("id").toLong()
        val page: Int = ctx.queryParam("page").let { arr -> if (arr.isEmpty()) 1 else arr[0].toInt() }
        val size: Int = ctx.queryParam("size").let { arr -> if (arr.isEmpty()) 20 else arr[0].toInt()  }

        val glyphRepo = FontGlyphRepository()
        val fontRepo = FontFaceRepository()
        val face = fontRepo.getById(fontFaceId)
        if (face == null) {
          ctx.json(Response.fail<PageDTO<List<FontGlyph>>>(FontFaceNotExist()))
        } else {
          val list = glyphRepo.getListByPage(fontFaceId, page, size)
          val dtoList = list.map { FontGlyphDTO.from(it) }
          ctx.json(Response.success(PageDTO(face.glyphCount, dtoList)))
        }
      }
  }
}
