package dev.yidafu.font2svg.dev.yidafu.font2svg.core

data class SvgGlyph(
    val viewBox: String,
    val path: String,
    val ascender: Int,
    val descender: Int,
    val underlinePos: Int,
    val underlineThickness: Int,
    val unitsPerEN: Int,
    val maxAdvanceHeight: Int,
    val maxAdvanceWidth: Int,
)