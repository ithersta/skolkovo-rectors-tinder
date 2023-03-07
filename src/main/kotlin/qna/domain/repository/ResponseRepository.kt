package qna.domain.repository

import qna.domain.entities.Response

interface ResponseRepository {
    fun add(questionId: Long, respondentId: Long): Long
    fun get(responseId: Long): Response?
    fun getRespondentsByQuestionId(questionId: Long, offset: Int, limit: Int): List<Long>
    fun has(respondentId: Long, questionId: Long): Boolean
}
