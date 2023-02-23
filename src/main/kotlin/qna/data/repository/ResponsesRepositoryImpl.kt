package qna.data.repository

import org.jetbrains.exposed.sql.insertAndGetId
import org.koin.core.annotation.Single
import qna.data.tables.Responses
import qna.domain.repository.ResponsesRepository

@Single
class ResponsesRepositoryImpl : ResponsesRepository {
    override fun add(questionId: Long, respondentId: Long): Long {
        return Responses.insertAndGetId {
            it[Responses.questionId] = questionId
            it[Responses.respondentId] = respondentId
        }.value
    }
}
