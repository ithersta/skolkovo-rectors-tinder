package qna.data.repository

import auth.data.tables.UserAreas
import auth.data.tables.Users
import mute.data.tables.MuteSettings
import org.jetbrains.exposed.sql.*
import org.jetbrains.exposed.sql.SqlExpressionBuilder.eq
import org.koin.core.annotation.Single
import qna.data.tables.QuestionAreas
import qna.data.tables.Questions
import qna.domain.entities.QuestionArea
import qna.domain.repository.UserAreasRepository

@Single
class UserAreasRepositoryImpl : UserAreasRepository {
    override fun getUsersByArea(questionArea: QuestionArea): List<Long> {
        val muteUsers = MuteSettings.slice(MuteSettings.userId).selectAll()
        return UserAreas.slice(UserAreas.userId).select { UserAreas.area eq questionArea }.except(muteUsers)
            .map { it[UserAreas.userId].value }
    }

    override fun getByArea(userId: Long, questionArea: Int): Map<Long, String> {
        return (Questions innerJoin QuestionAreas)
            .select(where = Users.id eq userId and Questions.isClosed.eq(false))
            .filter { questionArea == it[QuestionAreas.area].ordinal }
            .associate { it[Questions.id].value to it[Questions.subject] }
    }
}
