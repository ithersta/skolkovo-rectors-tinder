package qna.data.repository

import org.jetbrains.exposed.sql.SqlExpressionBuilder.eq
import org.jetbrains.exposed.sql.insertAndGetId
import org.jetbrains.exposed.sql.select
import org.koin.core.annotation.Single
import qna.data.tables.Responses
import qna.domain.entities.Response
import qna.domain.repository.ResponseRepository

@Single
class ResponseRepositoryImpl : ResponseRepository {
    override fun get(responseId: Long): Response? {
        return Responses.select { Responses.id eq responseId }.firstOrNull()?.let {
            Response(
                id = it[Responses.id].value,
                questionId = it[Responses.questionId].value,
                respondentId = it[Responses.respondentId].value
            )
        }
    }

    override fun getRespondentsByQuestionId(questionId: Long): List<Long> {
        return Responses
            .select(where = Responses.questionId eq questionId)
            .map { it[Responses.respondentId].value }
    }

    override fun add(questionId: Long, respondentId: Long): Long {
        return Responses.insertAndGetId {
            it[Responses.questionId] = questionId
            it[Responses.respondentId] = respondentId
        }.value
    }
}
