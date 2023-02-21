package auth.data.repository

import auth.data.tables.UserAreas
import auth.data.tables.Users
import auth.domain.entities.PhoneNumber
import auth.domain.entities.User
import auth.domain.repository.QuestionRepository
import org.jetbrains.exposed.sql.JoinType
import org.jetbrains.exposed.sql.select
import org.koin.core.annotation.Single
import qna.data.tables.Questions

@Single
class QuestionRepositoryImpl : QuestionRepository {
    override fun getQuestionTextByQuestionId(questionId: Long): String {
        return Questions.select { Questions.id eq questionId }.map { it[Questions.text] }[0]
    }

    override fun getUserInfoByQuestionId(questionId: Long): User.Details {
        return Questions.join(Users, JoinType.INNER, additionalConstraint = { Questions.authorId eq Users.id })
            .select { Questions.id eq questionId }.first().let {
                User.Details(
                    id = it[Users.id].value,
                    phoneNumber = PhoneNumber.of(it[Users.phoneNumber])!!,
                    name = it[Users.name],
                    city = it[Users.city],
                    professionalAreas = it[Users.professionalAreas],
                    job = it[Users.job],
                    organization = it[Users.organization],
                    activityDescription = it[Users.activityDescription],
                    areas = UserAreas.select { UserAreas.userId eq it[Users.id].value }
                        .map { resultRow -> resultRow[UserAreas.area] }.toSet()
                )
            }
    }
}
