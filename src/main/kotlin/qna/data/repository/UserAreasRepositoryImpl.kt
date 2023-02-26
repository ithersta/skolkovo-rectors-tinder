package qna.data.repository

import auth.data.tables.UserAreas
import mute.data.tables.MuteSettings
import notifications.data.tables.NotificationPreferences
import notifications.domain.entities.NotificationPreference
import org.jetbrains.exposed.sql.except
import org.jetbrains.exposed.sql.intersect
import org.jetbrains.exposed.sql.select
import org.jetbrains.exposed.sql.selectAll
import org.koin.core.annotation.Single
import qna.domain.entities.QuestionArea
import qna.domain.repository.UserAreasRepository

@Single
class UserAreasRepositoryImpl : UserAreasRepository {
    override fun getUsersByArea(questionArea: QuestionArea): List<Long> {
        val muteUsers = MuteSettings
            .slice(MuteSettings.userId)
            .selectAll()
        val rightAwayUsers = NotificationPreferences
            .slice(NotificationPreferences.userId)
            .select { NotificationPreferences.preference eq NotificationPreference.RightAway }
        return UserAreas
            .slice(UserAreas.userId)
            .select { UserAreas.area eq questionArea }
            .except(muteUsers)
            .intersect(rightAwayUsers)
            .map { it[UserAreas.userId].value }
    }
}
