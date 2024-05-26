package dev.yidafu.font2svg.web.model

import jakarta.persistence.Column
import jakarta.persistence.Entity
import jakarta.persistence.Table

@Entity
@Table(name = "font_task")
data class FontTask(
  @Column(name = "font_family")
  val fontFamily: String,

  @Column(name = "file_size")
  val fileSize: Long,

  @Column(name = "total_count")
  val tobalCount: Int,

  @Column(name = "generate_count")
  val generateCount: Int,

  @Column(name = "temp_filepath")
  val tempFilepath: String,

  @Column(name = "status")
  val status: Int,

  @Column(name = "font_face_id")
  val fontFaceId: Long,

  ) : FontBaseEntity() {
    constructor(): this("", 0, 0, 0, "", 0, 0)

    fun increase(count: Int): FontTask {
      return FontTask(fontFamily, fileSize, tobalCount, generateCount + count, tempFilepath, status, fontFaceId)
    }
  }
