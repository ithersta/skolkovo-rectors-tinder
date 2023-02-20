package qna.domain.repository

import qna.domain.entities.Question

interface QuestionRepository {
    fun add(question: Question.Details)
    fun get(userId: Long): Question.Details?
}
