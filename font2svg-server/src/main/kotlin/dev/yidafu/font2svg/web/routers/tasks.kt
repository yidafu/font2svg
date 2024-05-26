package dev.yidafu.font2svg.web.routers

import dev.yidafu.font2svg.core.FontSvgGenerator
import dev.yidafu.font2svg.web.beean.CreateTaskDTO
import dev.yidafu.font2svg.web.beean.WriteFileFailed
import dev.yidafu.font2svg.web.ext.chunked
import dev.yidafu.font2svg.web.ext.toCharacter
import dev.yidafu.font2svg.web.model.FontTask
import dev.yidafu.font2svg.web.repository.TaskRepository
import dev.yidafu.font2svg.web.ext.writeFileAsync
import dev.yidafu.font2svg.web.model.FontFace
import dev.yidafu.font2svg.web.model.FontGlyph
import dev.yidafu.font2svg.web.model.FontTaskStatus
import dev.yidafu.font2svg.web.repository.ConfigRepository
import dev.yidafu.font2svg.web.repository.FontFaceRepository
import dev.yidafu.font2svg.web.repository.FontGlyphRepository
import io.vertx.core.Vertx
import io.vertx.core.buffer.Buffer
import io.vertx.ext.web.Router
import io.vertx.ext.web.client.WebClient
import io.vertx.ext.web.codec.BodyCodec
import io.vertx.ext.web.handler.BodyHandler
import io.vertx.ext.web.validation.RequestPredicate
import io.vertx.ext.web.validation.builder.Bodies
import io.vertx.ext.web.validation.builder.ValidationHandlerBuilder
import io.vertx.json.schema.SchemaParser
import io.vertx.json.schema.SchemaRouter
import io.vertx.json.schema.SchemaRouterOptions
import io.vertx.json.schema.common.dsl.Schemas.objectSchema
import io.vertx.json.schema.common.dsl.Schemas.stringSchema
import io.vertx.kotlin.coroutines.*
import kotlinx.coroutines.channels.BufferOverflow
import kotlinx.coroutines.flow.buffer
import java.io.File
import java.net.URL
import java.nio.file.Paths
import kotlin.io.path.Path

const val FONT_GENERATION_EVENT = "font.generation"

inline fun CoroutineRouterSupport.createTaskRoute(vertx: Vertx): Router = Router.router(vertx).apply {
  coroutineRouter {
    route().handler(BodyHandler.create())
    val taskRepo = TaskRepository()
    val faceRepo = FontFaceRepository()
    val configRepo = ConfigRepository()
    val glyphRepo = FontGlyphRepository()

    val schemaRouter = SchemaRouter.create(vertx, SchemaRouterOptions())
    val schemaParser = SchemaParser.createDraft7SchemaParser(schemaRouter)

    get("/list").coHandler { ctx ->
      val list = taskRepo.findAll()
      ctx.json(list)
    }

    post("/")
      .handler(
        ValidationHandlerBuilder
          .create(schemaParser)
          .predicate(RequestPredicate.BODY_REQUIRED)
          .body(
            Bodies.json(
              objectSchema()
                .requiredProperty("fontFamily", stringSchema())
                .requiredProperty("fontUrl", stringSchema())
            )
          )
          .build()
      )
      .coHandler { ctx ->
        val createDTO = ctx.body().asJsonObject().mapTo(CreateTaskDTO::class.java)

        val client = WebClient.create(vertx)
        val reqUrl = URL(createDTO.fontUrl!!)
        val resp = client.get(reqUrl.port, reqUrl.host, reqUrl.path).`as`(BodyCodec.buffer()).send().coAwait()
        val fileBuf = resp.bodyAsBuffer()
        val fs = vertx.fileSystem()
        val tempFilepath = fs.createTempFile("font-task", Path(createDTO.fontUrl).fileName.toString()).coAwait()

        val writeSuccess = fs.writeFileAsync(tempFilepath, fileBuf)

        if (!writeSuccess) {
          ctx.json(WriteFileFailed())
          return@coHandler
        }
        val fontFile = File(tempFilepath)
        val generator = FontSvgGenerator(tempFilepath)
        generator.use {
          val allCharList = generator.getAllChars()

          val face = FontFace(createDTO.fontFamily!!, 0, fontFile.length())
          faceRepo.create(face)

          val task = FontTask(createDTO.fontFamily, fontFile.length(), allCharList.size, 0, tempFilepath, FontTaskStatus.Created, face.id!!)
          val newTask = taskRepo.createTask(task)

          vertx.eventBus().send(FONT_GENERATION_EVENT, newTask?.id)
          ctx.json(task)
        }
      }

    vertx.eventBus().consumer<Long>(FONT_GENERATION_EVENT)
      .handler { msg ->
        vertxFuture {
          val taskId = msg.body()
          taskRepo.updateStatus(taskId, FontTaskStatus.Generating)
          val task = taskRepo.findById(taskId) ?: return@vertxFuture
          val fontFaceId = task.fontFaceId
          val generator = FontSvgGenerator(task.tempFilepath)
          val fs = vertx.fileSystem()

          val fontFaceSvgDir = Paths.get(configRepo.svgStaticAssetsPath, task.fontFamily).toString()
          fs.mkdirs(fontFaceSvgDir)
          generator.use {svgGenerator ->
            svgGenerator.generateAll()
              .buffer(100, BufferOverflow.SUSPEND)
              .chunked(20)
              .collect { list ->
                  val glyphs = list.map {
                    val (charCode, svg) = it
                    val charText = charCode.toCharacter()
                    FontGlyph(fontFaceId, charText, charCode, svg)
                  }
                  glyphRepo.saveGlyphs(glyphs)

                  glyphs.forEach { glyph ->
                    fs.writeFileAsync(
                      Paths.get(fontFaceSvgDir, glyph.charCode.toString() + ".svg").toString(),
                      Buffer.buffer(glyph.svgContent)
                    )
                  }
                  faceRepo.updateGlyphCount(task.fontFaceId, list.size)
                  taskRepo.updateProcess(task.id!!, list.size)


                taskRepo.updateStatus(taskId, FontTaskStatus.Done)
              }
          }
        }
      }


    delete("/:id").handler {

    }

    get("/:id").handler {

    }
  }

}
