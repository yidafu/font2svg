package dev.yidafu.font2svg.web.repository

import dev.yidafu.font2svg.web.model.FontTask
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

  suspend fun findAll() {
    sessionFactory.withSession { session ->
      session.find(FontTask::class.java)
    }.toCompletableFuture().await()
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

  suspend fun updateProcess(taskId: Long, increaseCount: Int): Boolean {
      val task = findById(taskId) ?: return false
    println("task generate count ${task.generateCount}")
    sessionFactory.withSession { seession ->
        vertxFuture {
          val builder = sessionFactory.criteriaBuilder
          val update = builder.createCriteriaUpdate(FontTask::class.java)
          val from = update.from(FontTask::class.java)
          update.set(FontTask::generateCount.name, task.generateCount + increaseCount)
          update.where(builder.equal(from.get<String>(FontTask::id.name), task.id))

          seession.createQuery(update).executeUpdate().await()
        }.toCompletionStage()
      }.await()
    return true
  }
}
