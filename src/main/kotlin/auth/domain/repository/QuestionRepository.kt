package auth.domain.repository

import auth.domain.entities.Question

interface QuestionRepository {
    fun add(question: Question.Details)
    fun get(userId: Long): Question.Details?
}
