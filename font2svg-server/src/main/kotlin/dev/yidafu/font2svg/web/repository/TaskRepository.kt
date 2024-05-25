package dev.yidafu.font2svg.web.repository

import dev.yidafu.font2svg.web.model.FontTask
import org.hibernate.reactive.stage.Stage
import org.koin.core.component.KoinComponent
import org.koin.core.component.inject
import java.util.concurrent.CompletionStage

class TaskRepository(
) : KoinComponent {
  private val sessionFactory: Stage.SessionFactory by inject()

  fun findAll(): CompletionStage<MutableList<FontTask>> {
    return sessionFactory.withSession { session ->
      session.find(FontTask::class.java)
    }.whenComplete { result, err ->
        if (err != null) {
          emptyList<FontTask>()
        } else {
          result
        }
    }
  }
}
