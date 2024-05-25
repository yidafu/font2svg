package dev.yidafu.font2svg.web.model

import jakarta.persistence.Column
import jakarta.persistence.Entity
import jakarta.persistence.GeneratedValue
import jakarta.persistence.Id
import jakarta.persistence.Table

@Entity
@Table(name = "font_task")
data class FontTask(
  @Column(name = "font_family")
  val fontFamily: String,

  @Column(name = "file_size")
  val fileSize: Int,

  @Column(name = "total_count")
  val glyphCount: Int,

  @Column(name = "generate_count")
  val generateCount: Int,

  @Column(name = "status")
  val status: Int,

) : FontBaseEntity()
