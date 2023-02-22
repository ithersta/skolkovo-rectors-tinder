package auth.data.repository

import auth.data.tables.Users
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

    override fun getUserIdByQuestionId(questionId: Long): Long {
        return Questions
            .join(Users, JoinType.INNER, additionalConstraint = { Questions.authorId eq Users.id })
            .select { Questions.id eq questionId }
            .map { it[Users.id].value }[0]
    }
}
