package qna.data.repository

import auth.data.tables.UserAreas
import auth.data.tables.Users
import common.domain.Paginated
import kotlinx.datetime.Instant
import mute.data.tables.MuteSettings
import notifications.data.tables.NotificationPreferences
import notifications.domain.entities.NotificationPreference
import org.jetbrains.exposed.sql.*
import org.jetbrains.exposed.sql.SqlExpressionBuilder.neq
import org.koin.core.annotation.Single
import qna.data.tables.AcceptedResponses
import qna.data.tables.QuestionAreas
import qna.data.tables.Questions
import qna.data.tables.Responses
import qna.domain.entities.HideFrom.*
import qna.domain.entities.Question
import qna.domain.entities.QuestionArea
import qna.domain.repository.QuestionRepository

@Single
class QuestionRepositoryImpl : QuestionRepository {
    override fun add(question: Question): Long {
        val id = Questions.insertAndGetId {
            it[authorId] = question.authorId
            it[intent] = question.intent
            it[subject] = question.subject.value
            it[text] = question.text.value
            it[isClosed] = question.isClosed
            it[at] = question.at
            it[hideFrom] = question.hideFrom
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
            .leftJoin(AcceptedResponses)
            .slice(Questions.columns)
            .select {
                (Responses.hasBeenSent eq false) and
                    (Questions.isClosed eq false) and
                    (AcceptedResponses.responseId eq null)
            }
            .withDistinct()
            .map(::mapper)
    }

    override fun getQuestionsDigestPaginated(
        from: Instant,
        until: Instant,
        viewerUserId: Long,
        area: QuestionArea?,
        limit: Int,
        offset: Int
    ): Paginated<Question> {
        val (viewerCityId, viewerOrganizationId) = Users
            .select { Users.id eq viewerUserId }
            .map { it[Users.cityId] to it[Users.organizationId] }.first()
        val viewerAreas = UserAreas
            .slice(UserAreas.area)
            .select { UserAreas.userId eq viewerUserId }
        val query = {
            Questions
                .innerJoin(Users)
                .innerJoin(QuestionAreas)
                .slice(Questions.columns)
                .select {
                    Questions.at.between(from, until) and
                        (QuestionAreas.area inSubQuery viewerAreas) and
                        (Questions.isClosed eq false) and
                        (Questions.authorId neq viewerUserId) and
                        case()
                            .When(Questions.hideFrom eq NoOne, booleanLiteral(true))
                            .When(Questions.hideFrom eq SameCity, Users.cityId neq viewerCityId)
                            .When(
                                Questions.hideFrom eq SameOrganization,
                                Users.organizationId neq viewerOrganizationId
                            )
                            .Else(booleanLiteral(false))
                }
                .let { query ->
                    area?.let { query.andWhere { QuestionAreas.area eq it } } ?: query
                }
                .withDistinct()
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

    override fun getClosed(authorId: Long): List<Question> {
        return Questions
            .select { (Questions.authorId eq authorId) and (Questions.isClosed eq true) }
            .map(::mapper)
    }

    override fun getNotifiableUserIds(questionId: Long): List<Long> {
        val questionAreas = QuestionAreas
            .slice(QuestionAreas.area)
            .select { QuestionAreas.questionId eq questionId }
        val hideCondition = Questions
            .innerJoin(Users)
            .slice(Questions.hideFrom, Users.cityId, Users.organizationId)
            .select { Questions.id eq questionId }.first()
            .let {
                when (it[Questions.hideFrom]) {
                    NoOne -> (Users.id neq it[Questions.authorId])
                    SameCity -> (Users.cityId neq it[Users.cityId])
                    SameOrganization -> (Users.organizationId neq it[Users.organizationId])
                }
            }
        val muteUsers = MuteSettings
            .slice(MuteSettings.userId)
            .selectAll()
        val nonRightAwayUsers = NotificationPreferences
            .slice(NotificationPreferences.userId)
            .select { NotificationPreferences.preference neq NotificationPreference.RightAway }
        return UserAreas
            .innerJoin(Users)
            .slice(UserAreas.userId)
            .select { (UserAreas.area inSubQuery questionAreas) and Users.isApproved and hideCondition }
            .except(muteUsers)
            .except(nonRightAwayUsers)
            .map { it[UserAreas.userId].value }
    }

    companion object {
        fun mapper(row: ResultRow): Question {
            val questionId = row[Questions.id].value
            val areas = QuestionAreas
                .select { QuestionAreas.questionId eq questionId }
                .map { it[QuestionAreas.area] }.toSet()
            return Question(
                authorId = row[Questions.authorId].value,
                intent = row[Questions.intent],
                subject = Question.Subject.ofTruncated(row[Questions.subject]),
                text = Question.Text.ofTruncated(row[Questions.text]),
                isClosed = row[Questions.isClosed],
                areas = areas,
                at = row[Questions.at],
                hideFrom = row[Questions.hideFrom],
                id = row[Questions.id].value
            )
        }
    }
}
