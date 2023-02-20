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
    override fun getSubjectsByChatId(userId: Long): Set<String> {
        // todo: toSet временное решение тк удаляет все сферы которые похожи по названию, что не есть хорошо, но пока нет умных решений
        return UserAreas
            .join(QuestionAreas, JoinType.INNER, additionalConstraint = { UserAreas.area eq QuestionAreas.area })
            .join(Questions, JoinType.INNER, additionalConstraint = { Questions.id eq QuestionAreas.questionId })
            .select(where = UserAreas.userId eq userId and Questions.isClosed.eq(false))
            .map { it[Questions.subject] }
            .toSet()
    }
}