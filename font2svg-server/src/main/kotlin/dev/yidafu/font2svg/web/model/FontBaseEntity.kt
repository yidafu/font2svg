package dev.yidafu.font2svg.web.model

import jakarta.persistence.Column
import jakarta.persistence.GeneratedValue
import jakarta.persistence.Id
import jakarta.persistence.MappedSuperclass
import org.hibernate.annotations.CreationTimestamp
import org.hibernate.annotations.UpdateTimestamp
import java.sql.Date

@MappedSuperclass
open  class FontBaseEntity {

  @Id
  @GeneratedValue
  val id: Long? = null

  @CreationTimestamp
  @Column(name = "created_at")
  val createdAt: Date? = null


  @UpdateTimestamp
  @Column(name = "updated_at")
  val updatedAt: Date? = null
}
