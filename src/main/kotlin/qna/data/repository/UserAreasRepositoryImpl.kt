package qna.data.repository

import auth.data.tables.Users
import org.jetbrains.exposed.sql.*
import org.jetbrains.exposed.sql.SqlExpressionBuilder.eq
import org.koin.core.annotation.Single
import qna.data.tables.QuestionAreas
import qna.data.tables.Questions
import qna.data.tables.Responses
import qna.domain.entities.Question
import qna.domain.entities.QuestionArea
import qna.domain.repository.UserAreasRepository

@Single
class UserAreasRepositoryImpl : UserAreasRepository {
    override fun getQuestionsByUserIdAndUserArea(userId: Long, userArea: QuestionArea): List<Question> {
        val userCity: String = Users.select(where = Users.id eq userId).map { it[Users.city] }.first()
        val hiddenQuestions = Questions
            .innerJoin(Users)
            .slice(Questions.columns)
            .select {
                booleanLiteral(true) eq true
                // TODO (Questions.isBlockedCity eq true) and (Users.city eq userCity)
            }
        val questionsWithResponses = (Responses innerJoin Questions).slice(Questions.columns).select {
            Responses.respondentId eq userId
        }
        return Questions
            .join(Users, JoinType.INNER, additionalConstraint = { Questions.authorId eq Users.id })
            .join(QuestionAreas, JoinType.INNER, additionalConstraint = { Questions.id eq QuestionAreas.questionId })
            .slice(Questions.columns)
            .select {
                (Users.id neq userId) and (QuestionAreas.area eq userArea) and (Questions.isClosed eq false)
            }
            .except(questionsWithResponses)
            .except(hiddenQuestions)
            .map(QuestionRepositoryImpl::mapper)
    }
}
