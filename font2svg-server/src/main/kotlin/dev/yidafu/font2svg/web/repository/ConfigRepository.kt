package dev.yidafu.font2svg.web.repository

import dev.yidafu.font2svg.web.config.Font2SvgConfig
import org.koin.core.component.KoinComponent
import org.koin.core.component.inject

class ConfigRepository : KoinComponent {
  private val config: Font2SvgConfig by inject()

  val staticAssetsPath: String
    get() = config.staticAssetsPath

  val fontStaticAssetsPath: String
    get() = config.fontStaticAssetsPath

  val svgStaticAssetsPath: String
    get() = config.svgStaticAssetsPath
}
