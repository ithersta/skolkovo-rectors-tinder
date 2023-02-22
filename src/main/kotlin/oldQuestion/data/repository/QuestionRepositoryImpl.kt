package oldQuestion.data.repository

import auth.data.tables.Users
import oldQuestion.domain.repository.QuestionRepository
import org.jetbrains.exposed.sql.JoinType
import org.jetbrains.exposed.sql.SqlExpressionBuilder.eq
import org.jetbrains.exposed.sql.and
import org.jetbrains.exposed.sql.select
import org.koin.core.annotation.Single
import qna.data.tables.Questions
import qna.data.tables.Responses

@Single
class QuestionRepositoryImpl : QuestionRepository {
    override fun getSubjectsByUserIdAndIsClosed(userId: Long): Map<Long, String> {
        return Questions
            .select(Questions.isClosed eq true and (Questions.id eq userId))
            .associate { it[Questions.id].value to it[Questions.subject] }
    }

    override fun getRespondentIdByQuestion(questionId: Long): Map<String, String> {
        return Questions
            .join(Responses, JoinType.INNER, additionalConstraint = { Questions.id eq Responses.questionId })
            .join(Users, JoinType.INNER, additionalConstraint = { Questions.authorId eq Users.id })
            .select(Responses.questionId eq questionId)
            .associate { it[Users.name] to it[Users.phoneNumber] }
    }
}