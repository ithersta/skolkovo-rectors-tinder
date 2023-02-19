package auth.data.repository

import auth.data.tables.UserAreas
import mute.data.entities.MuteSettings
import auth.domain.repository.UserAreasRepository
import org.jetbrains.exposed.sql.except
import org.jetbrains.exposed.sql.select
import org.jetbrains.exposed.sql.selectAll
import org.koin.core.annotation.Single
import qna.domain.entities.QuestionArea

@Single
class UserAreasRepositoryImpl : UserAreasRepository {
    override fun getUsersByArea(questionArea: QuestionArea): List<Long> {
        // тут исключать mute пользователей
        val muteUsers = MuteSettings.slice(MuteSettings.userId).selectAll()
        return UserAreas.slice(UserAreas.userId).select { UserAreas.area eq questionArea }.except(muteUsers).map { it[UserAreas.userId].value }
    }
}
