package auth.data.repository

import auth.data.tables.UserAreas
import auth.domain.repository.UserAreasRepository
import org.jetbrains.exposed.sql.JoinType
import org.jetbrains.exposed.sql.SqlExpressionBuilder.eq
import org.jetbrains.exposed.sql.and
import org.jetbrains.exposed.sql.select
import org.koin.core.annotation.Single
import qna.data.tables.QuestionAreas
import qna.data.tables.Questions

@Single
class UserAreasRepositoryImpl : UserAreasRepository {
    override fun getSubjectsByChatId(userId: Long, offset: Long, limit: Int): Map<Long, String> {
        return UserAreas
            .join(QuestionAreas, JoinType.INNER, additionalConstraint = { UserAreas.area eq QuestionAreas.area })
            .join(Questions, JoinType.INNER, additionalConstraint = { Questions.id eq QuestionAreas.questionId })
            .select(where = UserAreas.userId eq userId and Questions.isClosed.eq(false))
            .limit(limit, offset)
            .filterNot { userId == it[Questions.authorId].value }
            .map { it[Questions.id].value to it[Questions.subject] }
            .distinct()
            .toMap()
    }
}
