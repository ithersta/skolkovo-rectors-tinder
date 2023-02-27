package qna.domain.repository

import qna.domain.entities.Question
import qna.domain.entities.QuestionArea

interface QuestionRepository {
    fun close(questionId: Long)
    fun add(question: Question): Long
    fun getById(questionId: Long): Question?
    fun getQuestionAreasByUserId(userId: Long): List<QuestionArea>
}
