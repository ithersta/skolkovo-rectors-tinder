package qna.data.repository

import auth.data.tables.UserAreas
import auth.data.tables.Users
import mute.data.tables.MuteSettings
import notifications.data.tables.NotificationPreferences
import notifications.domain.entities.NotificationPreference
import org.jetbrains.exposed.sql.*
import org.jetbrains.exposed.sql.SqlExpressionBuilder.eq
import org.jetbrains.exposed.sql.SqlExpressionBuilder.neq
import org.koin.core.annotation.Single
import qna.data.tables.QuestionAreas
import qna.data.tables.Questions
import qna.data.tables.Responses
import qna.domain.entities.Question
import qna.domain.entities.QuestionArea
import qna.domain.repository.UserAreasRepository

@Single
class UserAreasRepositoryImpl : UserAreasRepository {

    private fun getUsers(where: Op<Boolean>): List<Long> {
        val muteUsers = MuteSettings
            .slice(MuteSettings.userId)
            .selectAll()
        val nonRightAwayUsers = NotificationPreferences
            .slice(NotificationPreferences.userId)
            .select { NotificationPreferences.preference neq NotificationPreference.RightAway }
        return UserAreas
            .slice(UserAreas.userId)
            .select(where)
            .except(muteUsers)
            .except(nonRightAwayUsers)
            .map { it[UserAreas.userId].value }
    }

    override fun getUsersByArea(questionArea: QuestionArea): List<Long> {
        return getUsers(where = UserAreas.area eq questionArea)
    }

    override fun getFilteredUsersByArea(questionArea: QuestionArea, city: String): List<Long> {
        return getUsers(where = (UserAreas.area eq questionArea) and (Users.city neq city))
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

    override fun getQuestionsByUserIdAndUserArea(userId: Long, userArea: QuestionArea): List<Question> {
        val userCity: String = Users.select(where = Users.id eq userId).map { it[Users.city] }.first()
        val badQuestion = Questions
            .join(Users, JoinType.INNER, additionalConstraint = { Questions.authorId eq Users.id })
            .slice(Questions.columns)
            .select {
                (Questions.isBlockedCity eq true) and (Users.city neq userCity)
            }
        val doubled = (Responses innerJoin Questions).slice(Questions.columns).select {
            Responses.respondentId eq userId
        }
        return Questions
            .join(Users, JoinType.INNER, additionalConstraint = { Questions.authorId eq Users.id })
            .join(QuestionAreas, JoinType.INNER, additionalConstraint = { Questions.id eq QuestionAreas.questionId })
            .slice(Questions.columns)
            .select {
                (Users.id neq userId) and
                        (QuestionAreas.area eq userArea) and (Questions.isClosed eq false)
            }.except(doubled)
            .except(badQuestion)
            .map(::mapper)
    }
}
