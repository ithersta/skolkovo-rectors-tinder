package qna.data.repository

import auth.data.tables.UserAreas
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
        return UserAreas
            .slice(UserAreas.userId)
            .select { UserAreas.area eq questionArea }
            .except(muteUsers)
            .map { it[UserAreas.userId].value }
    }

    //добавить respondent
    override fun getSubjectsByArea(userId: Long, questionArea: Int): Map<Long, String> {
        return UserAreas.join(QuestionAreas, JoinType.INNER, additionalConstraint = { UserAreas.area eq QuestionAreas.area })
            .join(Questions, JoinType.INNER, additionalConstraint = { Questions.id eq QuestionAreas.questionId })
            .select(where = UserAreas.userId eq userId and Questions.isClosed.eq(false))
            .filterNot { userId == it[Questions.authorId].value }
            .filter { questionArea == it[QuestionAreas.area].ordinal }
            .map { it[Questions.id].value to it[Questions.subject] }
            .distinct()
            .toMap()
    }
}
