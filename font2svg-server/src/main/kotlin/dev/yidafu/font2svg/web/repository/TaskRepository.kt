package dev.yidafu.font2svg.web.repository

import dev.yidafu.font2svg.web.model.FontTask
import dev.yidafu.font2svg.web.model.FontTaskStatus
import io.vertx.kotlin.coroutines.coAwait
import io.vertx.kotlin.coroutines.vertxFuture
import kotlinx.coroutines.*
import kotlinx.coroutines.future.await
import org.hibernate.reactive.stage.Stage
import org.koin.core.component.KoinComponent
import org.koin.core.component.inject
import java.util.concurrent.CompletionStage
import java.util.concurrent.Future

class TaskRepository(
) : KoinComponent {
  private val sessionFactory: Stage.SessionFactory by inject()

  suspend fun findAll(): List<FontTask> {
    return sessionFactory.withSession { session ->
      val builder = sessionFactory.criteriaBuilder
      val query = builder.createQuery(FontTask::class.java)
      query.from(FontTask::class.java)
      session.createQuery(query).resultList
    }.toCompletableFuture().await() ?: emptyList()
  }

  suspend fun findById(id: Long): FontTask? {
    return sessionFactory.withSession { session ->
      session.find(FontTask::class.java, id)
    }.toCompletableFuture().await()
  }

  suspend fun createTask(task: FontTask): FontTask? {
    sessionFactory.withSession { session ->
      vertxFuture {
        session.persist(task).await()
        session.flush().await()
      }.toCompletionStage()

    }.toCompletableFuture().await()

    return task.id?.let { findById(it) }
  }

  suspend fun updateStatus(taskId: Long, status: FontTaskStatus): Boolean {
    val task = findById(taskId) ?: return false

    sessionFactory.withSession { seession ->
      vertxFuture {
        val builder = sessionFactory.criteriaBuilder
        val update = builder.createCriteriaUpdate(FontTask::class.java)
        val from = update.from(FontTask::class.java)
        update.set(FontTask::status.name, status)
        update.where(builder.equal(from.get<String>(FontTask::id.name), task.id))

        seession.createQuery(update).executeUpdate().await()
      }.toCompletionStage()
    }.await()
    return true
  }

  suspend fun updateProcess(taskId: Long, increaseCount: Int): Boolean {
      val task = findById(taskId) ?: return false

    sessionFactory.withSession { seession ->
        vertxFuture {
          val builder = sessionFactory.criteriaBuilder
          val update = builder.createCriteriaUpdate(FontTask::class.java)
          val from = update.from(FontTask::class.java)
          update.set(FontTask::generateCount.name, task.generateCount + increaseCount)
          update.where(builder.equal(from.get<String>(FontTask::id.name), task.id))

          seession.createQuery(update).executeUpdate().await()
          seession.flush().await()
        }.toCompletionStage()
      }.await()
    return true
  }
}
