package dev.yidafu.font2svg.web.service

import dev.yidafu.font2svg.core.FontSvgGenerator
import dev.yidafu.font2svg.web.ext.toSvgGlyph
import dev.yidafu.font2svg.web.repository.FontFaceRepository
import dev.yidafu.font2svg.web.repository.FontGlyphRepository
import org.koin.core.component.KoinComponent
import org.koin.core.component.inject

class FontService : KoinComponent {
  private val faceRepo: FontFaceRepository by inject()

  private val glyphRepo: FontGlyphRepository by inject()

  suspend fun getGlyph(
    fontFamily: String,
    charCode: Long,
    fontSize: Int,
    color: String,
    underline: Boolean= false,
  ): String? {
    val fontFace = faceRepo.getByName(fontFamily) ?: return null
    val glyphRepo = FontGlyphRepository()
    val glyph = glyphRepo.getByFontFaceAndCharCode(fontFace.id!!, charCode)
    if (glyph != null) {
      val svgGlyph = glyph.toSvgGlyph()
      val svgContent = FontSvgGenerator.glyphToSvgString(svgGlyph, fontSize, color, underline)
      return svgContent
    }
    return null
  }
}
