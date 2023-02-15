package auth.data.repository

import auth.data.tables.UserAreas
import auth.domain.repository.UserAreasRepository
import org.jetbrains.exposed.sql.select
import org.koin.core.annotation.Single
import qna.domain.entities.QuestionArea

@Single
class UserAreasRepositoryImpl: UserAreasRepository{
    override fun getUsersByArea(questionArea: QuestionArea, userId: Long): List<Long>{
        return UserAreas.select { UserAreas.area eq questionArea }.map { it[UserAreas.userId].value }.filterNot{it == userId}
    }
}