package qna.domain.repository

import common.domain.Paginated
import qna.domain.entities.Response

interface ResponseRepository {
    fun add(questionId: Long, respondentId: Long): Long?
    fun get(responseId: Long): Response?
    fun getRespondentsByQuestionId(questionId: Long, offset: Int, limit: Int): Paginated<Long>
    fun has(respondentId: Long, questionId: Long): Boolean
    fun count(questionId: Long): Int
    fun getAnyUnsent(questionId: Long): Response?
    fun markAsSent(responseId: Long)
}
