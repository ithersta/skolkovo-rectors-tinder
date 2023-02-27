package qna.data.repository

import auth.data.tables.Users
import org.jetbrains.exposed.sql.*
import org.jetbrains.exposed.sql.SqlExpressionBuilder.eq
import org.koin.core.annotation.Single
import qna.data.tables.QuestionAreas
import qna.data.tables.Questions
import qna.domain.entities.Question
import qna.domain.entities.QuestionArea
import qna.domain.repository.QuestionRepository

@Single
class QuestionRepositoryImpl : QuestionRepository {
    override fun add(question: Question): Long {
        val id = Questions.insertAndGetId {
            it[authorId] = question.authorId
            it[intent] = question.intent
            it[subject] = question.subject
            it[text] = question.text
            it[isClosed] = question.isClosed
        }
        QuestionAreas.batchInsert(question.areas) {
            this[QuestionAreas.questionId] = id
            this[QuestionAreas.area] = it
        }
        return id.value
    }

    override fun getById(questionId: Long): Question {
        val areas = QuestionAreas
            .select { QuestionAreas.questionId eq questionId }
            .map { it[QuestionAreas.area] }.toSet()
        return Questions.select { Questions.id eq questionId }.map {
            Question(
                authorId = it[Questions.authorId].value,
                intent = it[Questions.intent],
                subject = it[Questions.subject],
                text = it[Questions.text],
                isClosed = it[Questions.isClosed],
                areas = areas,
                id = it[Questions.id].value
            )
        }.first()
    }

    override fun getQuestionAreasByUserId(userId: Long): List<QuestionArea> {
        return QuestionAreas.join(
            Questions,
            JoinType.INNER,
            additionalConstraint = {QuestionAreas.questionId eq Questions.id})
            .join(Users, JoinType.INNER, additionalConstraint = { Questions.authorId eq Users.id})
                .select(where =  Users.id eq userId and Questions.isClosed.eq(false))
            .map{it[QuestionAreas.area]}
    }

    override fun close(questionId: Long) {
        Questions.update(where = { Questions.id eq questionId }) {
            it[Questions.isClosed] = true
        }
    }
}
