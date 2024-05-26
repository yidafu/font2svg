package dev.yidafu.font2svg.web.repository

import dev.yidafu.font2svg.web.model.FontFace
import dev.yidafu.font2svg.web.model.FontGlyph
import io.vertx.kotlin.coroutines.coAwait
import io.vertx.kotlin.coroutines.vertxFuture
import kotlinx.coroutines.future.await
import org.hibernate.reactive.stage.Stage
import org.koin.core.component.KoinComponent
import org.koin.core.component.inject

class FontFaceRepository : KoinComponent {
  private val sessionFactory: Stage.SessionFactory by inject()
  suspend fun create(face: FontFace) {
    val criteriaBuilder = sessionFactory.criteriaBuilder

    sessionFactory.withSession { session ->
      vertxFuture {
        val query = criteriaBuilder.createQuery(FontFace::class.java)
        val from = query.from(FontFace::class.java)

        val oldFace = session.createQuery(
          query.select(from)
            .where(criteriaBuilder.equal(
              from.get<String>(FontFace::name.name),
              face.name)
            )
        ).singleResultOrNull.toCompletableFuture().await()
        if (oldFace != null) {
          throw RuntimeException("font face ${face.name} already exists")
        }

        session.persist(face).await()

        session.flush().await()
      }.toCompletionStage()
    }.toCompletableFuture().await()
  }

  suspend fun saveGlyphs(glyphs: List<FontGlyph>) {
    sessionFactory.withSession {session ->
      vertxFuture {
        session.persist(*glyphs.toTypedArray()).await()
        session.flush().await()
      }.toCompletionStage()
    }.toCompletableFuture().await()
  }
}
