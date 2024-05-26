package dev.yidafu.font2svg.web.repository

import dev.yidafu.font2svg.web.model.FontFace
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

  suspend fun getById(faceId: Long): FontFace? {
    return sessionFactory.withSession { session ->
      session.find(FontFace::class.java, faceId)
    }.await()
  }

  suspend fun getByName(faceFamily: String): FontFace? {
    return sessionFactory.withSession { session ->
      val builder = sessionFactory.criteriaBuilder
      val query = builder.createQuery(FontFace::class.java)
      val from = query.from(FontFace::class.java)
      query.where(
        builder.equal(from.get<String>(FontFace::name.name), faceFamily)
      )
      session.createQuery(query).singleResultOrNull
    }.await()
  }
  suspend fun getAll(): List<FontFace> {
    return sessionFactory.withSession { session ->
      val builder = sessionFactory.criteriaBuilder
      val query = builder.createQuery(FontFace::class.java)
      query.from(FontFace::class.java)

      session.createQuery(query).resultList
    }.await() ?: emptyList()
  }


  suspend fun updateGlyphCount(faceId: Long, glyphCount: Int): Boolean {
    val face = getById(faceId) ?: return false

    sessionFactory.withSession { seession ->
      vertxFuture {
        val builder = sessionFactory.criteriaBuilder
        val update = builder.createCriteriaUpdate(FontFace::class.java)
        val from = update.from(FontFace::class.java)
        update.set(FontFace::glyphCount.name, face.glyphCount + glyphCount)
        update.where(builder.equal(from.get<String>(FontFace::id.name), faceId))

        seession.createQuery(update).executeUpdate().await()
        seession.flush().await()
      }.toCompletionStage()
    }.await()
    return true
  }
}
