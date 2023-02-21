package qna.domain.repository

import qna.domain.entities.Question

interface QuestionRepository {
    fun add(question: Question.Details): Long
    fun getUserId(questionId: Long): Long
    fun getTextById(questionId: Long): String
}
