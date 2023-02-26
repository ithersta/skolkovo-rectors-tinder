package qna.data.repository

import common.domain.Paginated
import kotlinx.datetime.Instant
import org.jetbrains.exposed.sql.*
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
            it[at] = question.at
        }
        QuestionAreas.batchInsert(question.areas) {
            this[QuestionAreas.questionId] = id
            this[QuestionAreas.area] = it
        }
        return id.value
    }

    override fun getById(questionId: Long): Question {
        return Questions.select { Questions.id eq questionId }.map(::mapper).first()
    }

    override fun getBetweenPaginated(
        from: Instant,
        until: Instant,
        excludeUserId: Long,
        limit: Int,
        offset: Int
    ): Paginated<Question> {
        val query = Questions
            .select {
                Questions.at.between(from, until) and
                    (Questions.authorId neq excludeUserId) and
                    (Questions.isClosed eq false)
            }
            .orderBy(Questions.at)
        return Paginated(
            slice = query.limit(limit, offset.toLong()).map(::mapper),
            count = query.count().toInt()
        )
    }

    override fun close(questionId: Long) {
        Questions.update(where = { Questions.id eq questionId }) {
            it[Questions.isClosed] = true
        }
    }

    private fun mapper(row: ResultRow): Question {
        val questionId = row[Questions.id].value
        val areas = QuestionAreas
            .select { QuestionAreas.questionId eq questionId }
            .map { it[QuestionAreas.area] }.toSet()
        return Question(
            authorId = row[Questions.authorId].value,
            intent = row[Questions.intent],
            subject = row[Questions.subject],
            text = row[Questions.text],
            isClosed = row[Questions.isClosed],
            areas = areas,
            at = row[Questions.at],
            id = row[Questions.id].value
        )
    }
}
