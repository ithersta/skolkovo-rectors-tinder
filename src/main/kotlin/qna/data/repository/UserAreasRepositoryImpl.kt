package qna.data.repository

import auth.data.tables.UserAreas
import mute.data.tables.MuteSettings
import notifications.data.tables.NotificationPreferences
import notifications.domain.entities.NotificationPreference
import org.jetbrains.exposed.sql.*
import org.jetbrains.exposed.sql.SqlExpressionBuilder.eq
import org.jetbrains.exposed.sql.SqlExpressionBuilder.neq
import org.koin.core.annotation.Single
import qna.data.tables.QuestionAreas
import qna.data.tables.Questions
import qna.domain.entities.QuestionArea
import qna.domain.repository.UserAreasRepository

@Single
class UserAreasRepositoryImpl : UserAreasRepository {
    override fun getUsersByArea(questionArea: QuestionArea): List<Long> {
        val muteUsers = MuteSettings
            .slice(MuteSettings.userId)
            .selectAll()
        val nonRightAwayUsers = NotificationPreferences
            .slice(NotificationPreferences.userId)
            .select { NotificationPreferences.preference neq NotificationPreference.RightAway }
        return UserAreas
            .slice(UserAreas.userId)
            .select { UserAreas.area eq questionArea }
            .except(muteUsers)
            .except(nonRightAwayUsers)
            .map { it[UserAreas.userId].value }
    }

    // todo: переписать на  list<Question>
    override fun getSubjectsByUserId(userId: Long, userArea: QuestionArea): Map<Long, String> {
        return (
            UserAreas.join(
                QuestionAreas,
                JoinType.INNER,
                additionalConstraint = { UserAreas.area eq QuestionAreas.area }
            ) innerJoin Questions
            )
            .select(
                (UserAreas.userId eq userId)
                    and (Questions.isClosed.eq(false))
                    and (Questions.authorId neq userId)
                    and (QuestionAreas.area eq userArea)
            ).associate { it[Questions.id].value to it[Questions.subject] }
    }
}
