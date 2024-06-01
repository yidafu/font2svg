package dev.yidafu.font2svg.web.repository

import dev.yidafu.font2svg.web.model.FontGlyph
import dev.yidafu.font2svg.web.model.FontTask
import io.vertx.kotlin.coroutines.vertxFuture
import kotlinx.coroutines.future.await
import org.hibernate.query.Page
import org.hibernate.reactive.stage.Stage
import org.koin.core.component.KoinComponent
import org.koin.core.component.inject


class FontGlyphRepository : KoinComponent{

  private val sessionFactory: Stage.SessionFactory by inject()

  suspend fun saveGlyphs(glyphs: List<FontGlyph>) {
    sessionFactory.withSession {session ->
      vertxFuture {
        session.persist(*glyphs.toTypedArray()).await()
        session.flush().await()
      }.toCompletionStage()
    }.toCompletableFuture().await()
  }

  suspend fun getByFontFaceAndGlyphId(faceId: Long, glyphId: Long): FontGlyph? {

    return sessionFactory.withTransaction { session ->
      val builder = sessionFactory.criteriaBuilder
      val query = builder.createQuery(FontGlyph::class.java)
      val from = query.from(FontGlyph::class.java)
      query.where(
        builder.equal(from.get<String>(FontGlyph::fontFaceId.name), faceId),
        builder.equal(from.get<String>(FontGlyph::id.name), glyphId),
      )

      session.createQuery(query).singleResultOrNull
    }.await()
  }

  suspend fun getByFontFaceAndCharCode(faceId: Long, charCode: Long): FontGlyph? {
    return sessionFactory.withSession { session ->
      val builder = sessionFactory.criteriaBuilder
      val query = builder.createQuery(FontGlyph::class.java)
      val from = query.from(FontGlyph::class.java)
      query.where(
        builder.equal(from.get<String>(FontGlyph::fontFaceId.name), faceId),
        builder.equal(from.get<String>(FontGlyph::charCode.name), charCode),
      )

      session.createQuery(query).singleResultOrNull
    }.await()
  }
  suspend fun getListByPage(faceId: Long, page: Int, size: Int): List<FontGlyph> {
    return sessionFactory.withSession { session ->
      val builder = sessionFactory.criteriaBuilder
      val query = builder.createQuery(FontGlyph::class.java)
      val from = query.from(FontGlyph::class.java)
      query.where(
        builder.equal(from.get<String>(FontGlyph::fontFaceId.name), faceId),
      )

      val pageObj = Page.page(size, page - 1)
      session.createQuery(query).setPage(pageObj).resultList
    }.await()
  }
}
