package qna.data.repository

import auth.data.tables.UserAreas
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

    override fun getQuestionsDigestPaginated(
        from: Instant,
        until: Instant,
        userId: Long,
        limit: Int,
        offset: Int
    ): Paginated<Question> {
        val areas = UserAreas
            .slice(UserAreas.area)
            .select { UserAreas.userId eq userId }
        val query = {
            Questions
                .innerJoin(QuestionAreas)
                .slice(Questions.columns)
                .select {
                    Questions.at.between(from, until) and
                        (Questions.authorId neq userId) and
                        (Questions.isClosed eq false)
                }
                .groupBy(*Questions.columns.toTypedArray())
                .having { QuestionAreas.area inSubQuery areas }
                .orderBy(Questions.at)
        }
        return Paginated(
            slice = query().limit(limit, offset.toLong()).map(::mapper),
            count = query().count().toInt()
        )
    }

    override fun close(questionId: Long) {
        Questions.update(where = { Questions.id eq questionId }) {
            it[Questions.isClosed] = true
        }
    }

    override fun getByUserId(userId: Long): List<Question> {
        return Questions
            .select { (Questions.authorId eq userId) and (Questions.isClosed eq false) }
            .map(::mapper)
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
