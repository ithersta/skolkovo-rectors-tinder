package qna.data.repository

import auth.data.tables.Users
import qna.domain.repository.ResponsesRepository
import org.jetbrains.exposed.sql.SqlExpressionBuilder.eq
import org.jetbrains.exposed.sql.select
import org.koin.core.annotation.Single
import qna.data.tables.Responses

@Single
class ResponsesRepositoryImpl : ResponsesRepository {
    override fun getRespondentIdByQuestion(questionId: Long): Map<String, String> {
        return (Users innerJoin Responses)
            .select(Responses.questionId eq questionId)
            .associate { it[Users.name] to it[Users.phoneNumber] }
    }
}
