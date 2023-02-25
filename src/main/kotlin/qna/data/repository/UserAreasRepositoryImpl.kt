package qna.data.repository

import auth.data.tables.UserAreas
import mute.data.tables.MuteSettings
import org.jetbrains.exposed.sql.except
import org.jetbrains.exposed.sql.select
import org.jetbrains.exposed.sql.selectAll
import org.koin.core.annotation.Single
import qna.domain.entities.QuestionArea
import qna.domain.repository.UserAreasRepository

@Single
class UserAreasRepositoryImpl : UserAreasRepository {
    override fun getUsersByArea(questionArea: QuestionArea): List<Long> {
        val muteUsers = MuteSettings.slice(MuteSettings.userId).selectAll()
        return UserAreas.slice(UserAreas.userId).select { UserAreas.area eq questionArea }.except(muteUsers).map { it[UserAreas.userId].value }
    }
}
