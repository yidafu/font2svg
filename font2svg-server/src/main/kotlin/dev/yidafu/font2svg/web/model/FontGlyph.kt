package dev.yidafu.font2svg.web.model

import jakarta.persistence.Column
import jakarta.persistence.Entity
import jakarta.persistence.Table

@Entity
@Table(name = "font_glyph")
class FontGlyph(
  @Column(name = "font_face_id")
  val fontFaceId: Long,
  @Column(name = "char_text")
  val charText: String,
  @Column(name = "char_code")
  val charCode: Long,
  @Column(name = "view_box")
  val viewBox: String,
  @Column(name = "svg_path", columnDefinition = "Text")
  val svgPath: String,
  @Column(name = "svg_ascender")
  val svgAscender: Int,
  @Column(name = "svg_descender")
  val svgDescender: Int,
) : FontBaseEntity() {

  constructor() : this(0,"", 0, "", "", 0, 0)
}
