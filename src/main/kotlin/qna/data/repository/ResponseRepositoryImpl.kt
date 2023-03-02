package qna.data.repository

import org.jetbrains.exposed.sql.*
import org.koin.core.annotation.Single
import qna.data.tables.Responses
import qna.domain.entities.Response
import qna.domain.repository.ResponseRepository

@Single
class ResponseRepositoryImpl : ResponseRepository {
    override fun get(responseId: Long): Response? {
        return Responses.select { Responses.id eq responseId }.firstOrNull()?.let(::mapper)
    }

    override fun has(respondentId: Long, questionId: Long): Boolean {
        return Responses
            .select { (Responses.questionId eq questionId) and (Responses.respondentId eq respondentId) }
            .empty().not()
    }

    override fun count(questionId: Long): Int {
        return Responses
            .select { Responses.questionId eq questionId }
            .count().toInt()
    }

    override fun getAnyUnsent(questionId: Long): Response? {
        return Responses
            .select { (Responses.hasBeenSent eq false) and (Responses.questionId eq questionId) }
            .orderBy(Responses.id)
            .limit(1)
            .firstOrNull()
            ?.let(::mapper)
    }

    override fun markAsSent(responseId: Long) {
        Responses.update(where = { Responses.id eq responseId }) {
            it[Responses.hasBeenSent] = true
        }
    }

    override fun add(questionId: Long, respondentId: Long): Long {
        return Responses.insertAndGetId {
            it[Responses.questionId] = questionId
            it[Responses.respondentId] = respondentId
        }.value
    }

    private fun mapper(it: ResultRow) = Response(
        id = it[Responses.id].value,
        questionId = it[Responses.questionId].value,
        respondentId = it[Responses.respondentId].value
    )
}
