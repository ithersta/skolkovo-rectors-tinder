package qna.data.repository

import auth.data.tables.UserAreas
import auth.data.tables.Users
import auth.domain.entities.PhoneNumber
import auth.domain.entities.User
import org.jetbrains.exposed.sql.ResultRow
import org.jetbrains.exposed.sql.SqlExpressionBuilder.eq
import org.jetbrains.exposed.sql.and
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

    override fun has(respondentId: Long, questionId: Long): Boolean {
        return Responses
            .select { (Responses.questionId eq questionId) and (Responses.respondentId eq respondentId) }
            .empty().not()
    }

    private fun mapper(row: ResultRow): User.Details {
        val userId = row[Users.id].value
        val areas = UserAreas
            .select { UserAreas.userId eq userId }
            .map { it[UserAreas.area] }.toSet()
        return User.Details(
            id = userId,
            phoneNumber = PhoneNumber.of(row[Users.phoneNumber])!!,
            name = row[Users.name],
            city = row[Users.city],
            job = row[Users.job],
            organization = row[Users.organization],
            activityDescription = row[Users.activityDescription],
            areas = areas,
            course = row[Users.course],
            organizationType = row[Users.organizationType]
        )
    }

    override fun getRespondentByQuestionId(questionId: Long): List<User.Details> {
        return (Users innerJoin Responses)
            .select(Responses.questionId eq questionId)
            .map(::mapper)
    }

    override fun add(questionId: Long, respondentId: Long): Long {
        return Responses.insertAndGetId {
            it[Responses.questionId] = questionId
            it[Responses.respondentId] = respondentId
        }.value
    }
}
