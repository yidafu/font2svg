package dev.yidafu.font2svg.web.config

import io.vertx.core.json.JsonObject
import io.vertx.kotlin.core.json.get
import java.nio.file.Path
import java.nio.file.Paths

class Font2SvgConfig {
  var staticAssetsPath =  run {
    val path = Paths.get(System.getProperty("user.dir"), "font2svg")
    ensureDirectory(path)
    path.toString()
  }

  var fontStaticAssetsPath = run {
    val path = Paths.get(staticAssetsPath, "fonts")
    ensureDirectory(path)
    path.toString()
  }

  var svgStaticAssetsPath = run {
    val path = Paths.get(staticAssetsPath, "svg")
    ensureDirectory(path)
    path.toString()
  }

  var serverPort = 8888

  private fun ensureDirectory(dir: Path) {
    val file = dir.toFile()
    if (!file.exists()) {
      file.mkdirs()
    }
  }
  fun updateStaticAssetsPath(path: String) {
    staticAssetsPath = path
    val fontDir = Paths.get(staticAssetsPath, "fonts")
    ensureDirectory(fontDir)
    fontStaticAssetsPath = fontDir.toString()

    val svgDIr = Paths.get(staticAssetsPath, "svg")
    ensureDirectory(svgDIr)
    svgStaticAssetsPath = svgDIr.toString()
  }

  companion object {
    fun build(obj: JsonObject): Font2SvgConfig {
      val config = Font2SvgConfig()
      obj.get<JsonObject?>("app")?.let { app ->
        app.get<JsonObject?>("assets")?.let { assets ->
          assets.getString("static-dir")?.let { staticDir ->
            config.updateStaticAssetsPath(staticDir)
          }
        }
      }
      obj.get<JsonObject?>("server")?.let { server ->
        server.getInteger("port")?.let { port ->
          config.serverPort = port
        }
      }

      return config
    }
  }
}
