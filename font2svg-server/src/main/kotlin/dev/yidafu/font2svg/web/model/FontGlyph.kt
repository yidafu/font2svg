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
  @Column(name = "font_ascender")
  val fontAscender: Int,
  @Column(name = "font_descender")
  val fontDescender: Int,
  @Column(name = "font_underline_position")
  val fontUnderlinePosition: Int,
  @Column(name = "font_underline_thickness")
  val fontUnderlineThickness: Int,
  @Column(name = "font_units_per_em")
  val fontUnitPerEm: Int,
  @Column(name = "font_max_advance_height")
  val fontMaxAdvanceHeight: Int,
  @Column(name = "font_max_advance_width")
  val fontMaxAdvanceWidth: Int,
) : FontBaseEntity() {
  constructor() : this(0, "", 0, "", "", 0, 0, 0, 0, 0, 0, 0)
}
