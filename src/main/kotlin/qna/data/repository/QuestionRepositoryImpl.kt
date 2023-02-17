package qna.data.repository

import org.jetbrains.exposed.sql.select
import org.jetbrains.exposed.sql.update
import org.koin.core.annotation.Single
import qna.data.tables.Questions
import qna.domain.entities.Question
import qna.domain.repository.QuestionRepository

@Single
class QuestionRepositoryImpl : QuestionRepository {
    override fun get(questionId: Long): Question? {
        return Questions.select { Questions.id eq questionId }.firstOrNull()?.let {
            Question(
                id = it[Questions.id].value,
                authorId = it[Questions.authorId].value,
                intent = it[Questions.intent],
                subject = it[Questions.subject],
                text = it[Questions.text],
                isClosed = it[Questions.isClosed]
            )
        }
    }

    override fun close(questionId: Long) {
        Questions.update(where = { Questions.id eq questionId }) {
            it[Questions.isClosed] = true
        }
    }
}
