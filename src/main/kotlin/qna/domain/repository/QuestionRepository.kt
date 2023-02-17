package qna.domain.repository

import qna.domain.entities.Question

interface QuestionRepository {
    fun get(questionId: Long): Question?
    fun close(questionId: Long)
}
