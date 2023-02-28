package qna.data.repository

import org.jetbrains.exposed.sql.*
import org.koin.core.annotation.Single
import qna.data.tables.Responses
import qna.domain.entities.Response
import qna.domain.entities.ResponseRange
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

    override fun getUnsentRange(questionId: Long): ResponseRange? {
        return Responses
            .slice(Responses.questionId, Responses.id.min(), Responses.id.max())
            .select { (Responses.questionId eq questionId) and (Responses.hasBeenSent eq false) }
            .firstOrNull()
            ?.let {
                val min = it[Responses.id.min()]?.value ?: return@let null
                val max = it[Responses.id.max()]?.value ?: return@let null
                ResponseRange(min..max, it[Responses.questionId].value)
            }
    }

    override fun getUnsentRanges(): List<ResponseRange> {
        return Responses
            .slice(Responses.questionId, Responses.id.min(), Responses.id.max())
            .select { Responses.hasBeenSent eq false }
            .groupBy(Responses.questionId)
            .map {
                ResponseRange(
                    it[Responses.id.min()]!!.value..it[Responses.id.max()]!!.value,
                    it[Responses.questionId].value
                )
            }
    }

    override fun markAsSent(responseRange: ResponseRange) {
        Responses.update(where = {
            Responses.id.between(
                responseRange.idRange.first,
                responseRange.idRange.last
            ) and (Responses.questionId eq responseRange.questionId)
        }) {
            it[Responses.hasBeenSent] = true
        }
    }

    override fun add(questionId: Long, respondentId: Long): Long {
        return Responses.insertAndGetId {
            it[Responses.questionId] = questionId
            it[Responses.respondentId] = respondentId
        }.value
    }
}
