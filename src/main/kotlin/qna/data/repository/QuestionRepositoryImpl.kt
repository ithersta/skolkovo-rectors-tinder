package qna.data.repository

import qna.domain.entities.Question
import org.jetbrains.exposed.sql.batchInsert
import org.jetbrains.exposed.sql.insertAndGetId
import qna.data.tables.QuestionAreas
import qna.data.tables.Questions
import qna.domain.repository.QuestionRepository

class QuestionRepositoryImpl : QuestionRepository {
    override fun add(question: Question.Details) {
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
    }

    override fun get(userId: Long): Question.Details? {
        TODO("Not yet implemented")
    }
}
