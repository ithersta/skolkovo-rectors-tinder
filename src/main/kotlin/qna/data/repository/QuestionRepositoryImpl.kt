package qna.data.repository

import org.jetbrains.exposed.sql.batchInsert
import org.jetbrains.exposed.sql.insertAndGetId
import org.jetbrains.exposed.sql.select
import org.jetbrains.exposed.sql.update
import org.koin.core.annotation.Single
import qna.data.tables.QuestionAreas
import qna.data.tables.Questions
import qna.domain.entities.Question
import qna.domain.repository.QuestionRepository

@Single
class QuestionRepositoryImpl : QuestionRepository {
    override fun add(question: Question): Long {
        val id = Questions.insertAndGetId {
            it[authorId] = question.authorId
            it[intent] = question.intent
            it[subject] = question.subject
            it[text] = question.text
            it[isClosed] = question.isClosed
        }
        QuestionAreas.batchInsert(question.areas) {
            this[QuestionAreas.questionId] = id
            this[QuestionAreas.area] = it
        }
        return id.value
    }

    override fun getById(questionId: Long): Question {
        val areas = QuestionAreas
            .select { QuestionAreas.questionId eq questionId }
            .map { it[QuestionAreas.area] }.toSet()
        return Questions.select { Questions.id eq questionId }.map {
            Question(
                authorId = it[Questions.authorId].value,
                intent = it[Questions.intent],
                subject = it[Questions.subject],
                text = it[Questions.text],
                isClosed = it[Questions.isClosed],
                areas = areas,
                id = it[Questions.id].value
            )
        }.first()
    }

    override fun close(questionId: Long) {
        Questions.update(where = { Questions.id eq questionId }) {
            it[Questions.isClosed] = true
        }
    }
}
