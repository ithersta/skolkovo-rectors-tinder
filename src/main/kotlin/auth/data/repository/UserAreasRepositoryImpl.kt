package auth.data.repository

import auth.data.tables.UserAreas
import auth.domain.repository.UserAreasRepository
import org.jetbrains.exposed.sql.select
import qna.domain.entities.QuestionArea

class UserAreasRepositoryImpl: UserAreasRepository{
    override fun getAllByArea(areaQ: QuestionArea): List<Long>{
        return UserAreas.select { UserAreas.area eq areaQ }
            .map { it[UserAreas.userId].value }
    }

}
