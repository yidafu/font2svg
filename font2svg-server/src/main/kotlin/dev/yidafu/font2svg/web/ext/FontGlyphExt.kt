package dev.yidafu.font2svg.web.ext

import dev.yidafu.font2svg.dev.yidafu.font2svg.core.SvgGlyph
import dev.yidafu.font2svg.web.model.FontGlyph

fun FontGlyph.toSvgGlyph(): SvgGlyph {
  return SvgGlyph(viewBox, svgPath, svgAscender, svgDescender)
}
