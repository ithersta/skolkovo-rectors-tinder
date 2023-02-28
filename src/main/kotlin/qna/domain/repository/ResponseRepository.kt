package qna.domain.repository

import qna.domain.entities.Response
import qna.domain.entities.ResponseRange

interface ResponseRepository {
    fun add(questionId: Long, respondentId: Long): Long
    fun get(responseId: Long): Response?
    fun has(respondentId: Long, questionId: Long): Boolean
    fun count(questionId: Long): Int
    fun getUnsentRanges(): List<ResponseRange>
    fun getUnsentRange(questionId: Long): ResponseRange?
    fun markAsSent(responseRange: ResponseRange)
}
