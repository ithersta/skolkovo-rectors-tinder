package qna.domain.repository

import auth.domain.entities.User
import common.domain.Paginated
import qna.domain.entities.Response

interface ResponseRepository {
    fun add(questionId: Long, respondentId: Long): Long
    fun get(responseId: Long): Response?
    fun getRespondentsByQuestionId(questionId: Long, offset: Int, limit: Int): Paginated<Long>
    fun has(respondentId: Long, questionId: Long): Boolean
    fun getRespondentByQuestionId(questionId: Long): List<User.Details>
}
