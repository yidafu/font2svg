package dev.yidafu.font2svg.web.ext

import dev.yidafu.font2svg.dev.yidafu.font2svg.core.SvgGlyph
import dev.yidafu.font2svg.web.model.FontGlyph

fun FontGlyph.toSvgGlyph(): SvgGlyph {
  return SvgGlyph(
    viewBox,
    svgPath,
    fontAscender,
    fontDescender,
    fontUnderlinePosition,
    fontUnderlineThickness,
    fontUnitPerEm,
    fontMaxAdvanceHeight,
    fontMaxAdvanceWidth
  )
}


fun SvgGlyph.toFontGlyph(fontFaceId: Long, charCode: Long): FontGlyph {
  return FontGlyph(
    fontFaceId,
    charCode.toCharacter(),
    charCode,
    viewBox,
    path,
    ascender,
    descender,
    underlinePos,
    underlineThickness,
    unitsPerEN,
    maxAdvanceHeight,
    maxAdvanceWidth
  )
}
