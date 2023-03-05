package qna.domain.repository

import qna.domain.entities.Response

interface ResponseRepository {
    fun add(questionId: Long, respondentId: Long): Long?
    fun get(responseId: Long): Response?
    fun has(respondentId: Long, questionId: Long): Boolean
    fun count(questionId: Long): Int
    fun getAnyUnsent(questionId: Long): Response?
    fun markAsSent(responseId: Long)
}
