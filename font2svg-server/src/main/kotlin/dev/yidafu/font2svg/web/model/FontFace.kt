package dev.yidafu.font2svg.web.model

import jakarta.persistence.Column
import jakarta.persistence.Entity
import jakarta.persistence.Table

@Entity
@Table(name = "font_face")
class FontFace(
  @Column
  val name: String,
  @Column(name = "glyph_count")
  val glyphCount: Int,
  @Column(name = "file_size")
  val fileSize: Long,
  @Column(name = "preview_text")
  val previewText: String,
  @Column(name = "download_url")
  val downloadUrl: String,
) : FontBaseEntity() {
  constructor() : this("", 0, 0, "", "")
}
