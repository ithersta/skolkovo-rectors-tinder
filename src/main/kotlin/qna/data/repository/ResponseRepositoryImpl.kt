package qna.data.repository

import auth.data.tables.Users
import auth.data.tables.toDomainModel
import auth.domain.entities.User
import common.domain.Paginated
import org.jetbrains.exposed.sql.*
import org.jetbrains.exposed.sql.ResultRow
import org.jetbrains.exposed.sql.SqlExpressionBuilder.eq
import org.koin.core.annotation.Single
import qna.data.tables.AcceptedResponses
import qna.data.tables.Responses
import qna.domain.entities.Response
import qna.domain.repository.ResponseRepository

@Single
class ResponseRepositoryImpl : ResponseRepository {
    override fun get(responseId: Long): Response? {
        return Responses.select { Responses.id eq responseId }.firstOrNull()?.let(::mapper)
    }

    override fun getByQuestionId(questionId: Long, offset: Int, limit: Int): Paginated<Response> {
        val list = {
            Responses
                .select(Responses.questionId eq questionId)
        }
        return Paginated(
            slice = list().limit(limit, offset.toLong()).map(::mapper),
            count = list().count().toInt()
        )
    }

    override fun has(respondentId: Long, questionId: Long): Boolean {
        return Responses
            .select { (Responses.questionId eq questionId) and (Responses.respondentId eq respondentId) }
            .empty().not()
    }

    override fun getRespondentByQuestionId(questionId: Long): List<User.Details> {
        val query = (Users innerJoin Responses)
            .select(Responses.questionId eq questionId)
        return Users.Entity.wrapRows(query).map(Users.Entity::toDomainModel)
    }

    override fun countForQuestion(questionId: Long): Int {
        return Responses
            .select { Responses.questionId eq questionId }
            .count().toInt()
    }

    override fun getAnyUnsent(questionId: Long): Response? {
        return Responses
            .leftJoin(AcceptedResponses)
            .select {
                (Responses.hasBeenSent eq false) and
                    (Responses.questionId eq questionId) and
                    (AcceptedResponses.responseId eq null)
            }
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

    override fun add(questionId: Long, respondentId: Long): Long? {
        return Responses.insertIgnoreAndGetId {
            it[Responses.questionId] = questionId
            it[Responses.respondentId] = respondentId
        }?.value
    }

    private fun mapper(it: ResultRow) = Response(
        id = it[Responses.id].value,
        questionId = it[Responses.questionId].value,
        respondentId = it[Responses.respondentId].value
    )
}
