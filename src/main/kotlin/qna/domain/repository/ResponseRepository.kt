package qna.domain.repository

import auth.domain.entities.User
import common.domain.Paginated
import qna.domain.entities.Response

interface ResponseRepository {
    fun add(questionId: Long, respondentId: Long): Long?
    fun get(responseId: Long): Response?
    fun getByQuestionId(questionId: Long, offset: Int, limit: Int): Paginated<Response>
    fun has(respondentId: Long, questionId: Long): Boolean
    fun countForQuestion(questionId: Long): Int
    fun getAnyUnsent(questionId: Long): Response?
    fun markAsSent(responseId: Long)
    fun getRespondentByQuestionId(questionId: Long): List<User.Details>
}
