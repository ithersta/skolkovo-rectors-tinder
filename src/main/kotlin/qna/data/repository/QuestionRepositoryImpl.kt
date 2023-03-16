package qna.data.repository

import auth.data.tables.UserAreas
import auth.data.tables.Users
import common.domain.Paginated
import kotlinx.datetime.Instant
import org.jetbrains.exposed.sql.*
import org.jetbrains.exposed.sql.SqlExpressionBuilder.eq
import org.koin.core.annotation.Single
import qna.data.tables.QuestionAreas
import qna.data.tables.Questions
import qna.data.tables.Responses
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

    override fun getWithUnsentResponses(): List<Question> {
        return Questions
            .innerJoin(Responses)
            .slice(Questions.columns)
            .select { (Responses.hasBeenSent eq false) and (Questions.isClosed eq false) }
            .withDistinct()
            .map(::mapper)
    }

    override fun getQuestionsDigestPaginated(
        from: Instant,
        until: Instant,
        userId: Long,
        limit: Int,
        offset: Int
    ): Paginated<Question> {
        val authorCity = Users.select(where = Users.id eq userId).map { it[Users.city] }.first()
        val badQuestion = Questions
            .join(Users, JoinType.INNER, additionalConstraint = { Questions.authorId eq Users.id })
            .slice(Questions.columns)
            .select {
                (Questions.isBlockedCity eq true) and (Users.city neq authorCity)
            }
        val areas = UserAreas
            .slice(UserAreas.area)
            .select { UserAreas.userId eq userId }
        val query = {
            Questions
                .innerJoin(QuestionAreas)
                .slice(Questions.columns)
                .select {
                    Questions.at.between(from, until) and
                            (Questions.authorId neq userId)
                }
                .groupBy(*Questions.columns.toTypedArray())
                .having { QuestionAreas.area inSubQuery areas }
                .except(badQuestion)
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

    override fun closeOlderThan(instant: Instant) {
        Questions.update(where = { (Questions.isClosed eq false) and (Questions.at less instant) }) {
            it[Questions.isClosed] = true
        }
    }

    override fun getByUserId(userId: Long, offset: Int, limit: Int): Paginated<Question> {
        val list = {
            Questions
                .select { (Questions.authorId eq userId) and (Questions.isClosed eq false) }
        }
        return Paginated(
            slice = list().limit(limit, offset.toLong()).map(::mapper),
            count = list().count().toInt()
        )
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
            isBlockedCity = row[Questions.isBlockedCity],
            id = row[Questions.id].value
        )
    }

    override fun getSubjectsByUserIdAndIsClosed(userId: Long): List<Question> {
        return Questions
            .select { (Questions.authorId eq userId) and (Questions.isClosed eq true) }
            .map(::mapper)
    }
}
