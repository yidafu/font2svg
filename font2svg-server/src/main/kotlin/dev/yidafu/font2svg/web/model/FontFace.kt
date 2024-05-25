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
  val fileSize: Int,
) : FontBaseEntity()
