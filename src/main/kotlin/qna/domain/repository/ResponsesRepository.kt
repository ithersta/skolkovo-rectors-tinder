package qna.domain.repository

interface ResponsesRepository {
    fun add(questionId: Long, respondentId: Long): Long
}