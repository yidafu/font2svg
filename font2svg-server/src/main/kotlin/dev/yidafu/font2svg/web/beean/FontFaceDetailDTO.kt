package dev.yidafu.font2svg.web.beean

import dev.yidafu.font2svg.web.model.FontFace
import dev.yidafu.font2svg.web.model.FontTask

data class FontFaceDetailDTO(
  val id: Long,
  val name: String,
  val fileSize: Long,
  val glyphCount: Int,
  val previewText: String,
  val downloadUrl: String,
  val tasks: List<FontTask>
) {
  companion object {
    fun from(fontFace: FontFace, tasks: List<FontTask>): FontFaceDetailDTO {
      return FontFaceDetailDTO(
         fontFace.id ?: -1,
         fontFace.name,
         fontFace.fileSize,
         fontFace.glyphCount,
         fontFace.previewText,
        fontFace.downloadUrl,
        tasks
      )
    }
  }
}
