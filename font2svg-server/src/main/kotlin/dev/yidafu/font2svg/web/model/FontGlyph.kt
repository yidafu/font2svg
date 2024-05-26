package dev.yidafu.font2svg.web.model

import jakarta.persistence.Column
import jakarta.persistence.Entity
import jakarta.persistence.Table
import org.hibernate.annotations.Type

@Entity
@Table(name = "font_glyph")
class FontGlyph(
  @Column(name = "font_face_id")
  val fontFaceId: Long,
  @Column(name = "char_text")
  val charText: String,
  @Column(name = "char_code")
  val charCode: Long,
  @Column(name = "svg_content", columnDefinition = "Text")
  val svgContent: String,
) : FontBaseEntity() {

  constructor() : this(0,"", 0, "")
}
