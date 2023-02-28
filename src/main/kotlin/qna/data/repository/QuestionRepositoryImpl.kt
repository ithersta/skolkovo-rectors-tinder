package qna.data.repository

import org.jetbrains.exposed.sql.*
import org.jetbrains.exposed.sql.SqlExpressionBuilder.eq
import org.koin.core.annotation.Single
import qna.data.tables.QuestionAreas
import qna.data.tables.Questions
import qna.domain.entities.Question
import qna.domain.entities.QuestionArea
import qna.domain.repository.QuestionRepository
import java.util.stream.Collectors

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
        return Questions.select { Questions.id eq questionId }.map(::mapper).first()
    }

    override fun getQuestionAreasByUserId(userId: Long): List<QuestionArea> {
        return (QuestionAreas innerJoin Questions)
            .select(Questions.authorId eq userId and Questions.isClosed.eq(false))
            .map { it[QuestionAreas.area] }
    }

    override fun close(questionId: Long) {
        Questions.update(where = { Questions.id eq questionId }) {
            it[Questions.isClosed] = true
        }
    }
    override fun getByArea(userId: Long, questionArea: QuestionArea): List<Question> {
        val questionsId = (Questions innerJoin QuestionAreas)
            .select(
                (Questions.authorId eq userId)
                    and (Questions.isClosed.eq(false))
                    and (QuestionAreas.area eq questionArea)
            )
            .map { it[QuestionAreas.questionId].value }
        return questionsId.stream()
            .map { Questions.select { Questions.id eq it }.map(::mapper).first() }
            .collect(Collectors.toList())
    }
    private fun mapper(row: ResultRow): Question {
        val questionId = row[Questions.id].value
        val areas = QuestionAreas
            .select { QuestionAreas.questionId eq questionId }
            .map { it[QuestionAreas.area] }.toSet()
        return Question(
            authorId = row[Questions.authorId].value,
            intent = row[Questions.intent],
            subject = row[Questions.subject],
            text = row[Questions.text],
            isClosed = row[Questions.isClosed],
            areas = areas,
            id = row[Questions.id].value
        )
    }
}
