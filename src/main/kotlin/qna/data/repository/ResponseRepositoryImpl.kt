package qna.data.repository

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
}
