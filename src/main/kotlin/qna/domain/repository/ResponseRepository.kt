package qna.domain.repository

import auth.domain.entities.User
import qna.domain.entities.Response

interface ResponseRepository {
    fun add(questionId: Long, respondentId: Long): Long
    fun get(responseId: Long): Response?
    fun has(respondentId: Long, questionId: Long): Boolean
    fun getRespondentByQuestionId(questionId: Long): List<User.Details>
}
