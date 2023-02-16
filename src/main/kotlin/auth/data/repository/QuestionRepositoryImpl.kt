package auth.data.repository

import auth.domain.entities.Question
import auth.domain.repository.QuestionRepository
import org.jetbrains.exposed.sql.batchInsert
import org.jetbrains.exposed.sql.insert
import qna.data.tables.QuestionAreas
import qna.data.tables.Questions

class QuestionRepositoryImpl : QuestionRepository {
    override fun add(question: Question.Details) {
        Questions.insert{
            it[authorId] = question.authorId
            it[intent] = question.intent
            it[subject] = question.subject
            it[text] = question.text
        }
        QuestionAreas.batchInsert(question.areas){
            //тут нужно передавать id вопроса, пока не уверена в правильности
            this[QuestionAreas.questionId] = Questions.id
            this[QuestionAreas.area] = it
        }
    }

    override fun get(userId: Long): Question.Details? {
        TODO("Not yet implemented")
    }
}