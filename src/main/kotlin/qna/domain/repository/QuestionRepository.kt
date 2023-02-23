package qna.domain.repository

import qna.domain.entities.Question

interface QuestionRepository {
    fun add(question: Question): Long
    fun getUserId(questionId: Long): Long
    fun getById(questionId: Long): Question
}
