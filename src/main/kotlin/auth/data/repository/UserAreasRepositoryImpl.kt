package auth.data.repository

import auth.data.tables.UserAreas
import auth.domain.repository.UserAreasRepository
import org.jetbrains.exposed.sql.except
import org.jetbrains.exposed.sql.select
import org.koin.core.annotation.Single
import qna.domain.entities.QuestionArea

@Single
class UserAreasRepositoryImpl : UserAreasRepository {
    override fun getUsersByArea(questionArea: QuestionArea, userId: Long): List<Long> {
        //тут исключать mute пользователей
        val exceptUser = UserAreas.select { UserAreas.userId eq userId }
        return UserAreas.select { UserAreas.area eq questionArea }.except(exceptUser).map { it[UserAreas.userId].value }
    }
}
