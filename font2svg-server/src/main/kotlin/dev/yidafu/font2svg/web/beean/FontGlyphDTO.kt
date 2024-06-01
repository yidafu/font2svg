package dev.yidafu.font2svg.web.beean

import dev.yidafu.font2svg.core.FontSvgGenerator
import dev.yidafu.font2svg.web.ext.toSvgGlyph
import dev.yidafu.font2svg.web.model.FontGlyph

class FontGlyphDTO(
  val charCode: Long,
  val charText: String,
  val svg: String,
  val svgAscender: Int,
  val svgDescender: Int,
) {
  companion object {
    fun from(glyph: FontGlyph): FontGlyphDTO {
      return FontGlyphDTO(
        charCode = glyph.charCode,
        charText = glyph.charText,
        svg = FontSvgGenerator.glyphToSvgString(glyph.toSvgGlyph(), 16, "currentColor"),
        svgAscender = glyph.svgAscender,
        svgDescender = glyph.svgDescender,
      )
    }
  }
}
